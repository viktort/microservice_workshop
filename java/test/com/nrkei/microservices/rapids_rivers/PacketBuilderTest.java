package com.nrkei.microservices.rapids_rivers;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Ensures that the packet.PacketBuilder successfully creates modifiable Packets
public class PacketBuilderTest {

    private final static String SOLUTION_STRING =
            "{\"need\":\"car_rental_offer\"," +
                    "\"user_id\":456," +
                    "\"solutions\":[" +
                    "{\"offer\":\"15% discount\"}," +
                    "{\"offer\":\"500 extra points\"}," +
                    "{\"offer\":\"free upgrade\"}" +
                    "]," +
                    "\"frequent_renter\":\"\"," +
                    "\"system.read_count\":2," +
                    "\"contributing_services\":[]}";

    private final static String MISSING_COMMA =
            "{\"frequent_renter\":\"\" \"read_count\":2}";

    private final static String NEED_KEY = "need";
    private final static String KEY_TO_BE_ADDED = "key_to_be_added";
    private static final String EMPTY_ARRAY_KEY = "contributing_services";
    private static final String INTERESTING_KEY = "frequent_renter";
    private static final String SOLUTIONS_KEY = "solutions";

    private PacketBuilder builder;
    private Collection solutions;

    @Before
    public void setUp() throws Exception {
        builder = new PacketBuilder(SOLUTION_STRING);
    }

    @Test
    public void validJsonExtracted() throws Exception {
        assertTrue(builder.isPacketValid());
    }

    @Test
    public void invalidJsonFormat() throws Exception {  // missing comma
        assertFalse(new PacketBuilder(MISSING_COMMA).isPacketValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void packetInaccessibleIfInvalid() throws Exception {  // missing comma
        new PacketBuilder(MISSING_COMMA).result();
    }

    @Test
    public void requiredKeyExists() throws Exception {
        builder.require(NEED_KEY);
        assertEquals("car_rental_offer", builder.result().get(NEED_KEY));
    }

    @Test
    public void missingRequiredKey() throws Exception {
        builder.require("missing_key");
        assertFalse(builder.isPacketValid());
    }

    @Test
    public void requiredKeyChangeable() throws Exception {
        builder.require(NEED_KEY);
        Packet p = builder.result();
        assertEquals("car_rental_offer", p.get(NEED_KEY));
        p.put(NEED_KEY, "airline_offer");
        assertEquals("airline_offer", p.get(NEED_KEY));
    }

    @Test
    public void forbiddenFieldChangeable() throws Exception {
        builder.forbid(KEY_TO_BE_ADDED);
        Packet p = builder.result();
        assertNull(p.get(KEY_TO_BE_ADDED));
        p.put(KEY_TO_BE_ADDED, "Bingo!");
        assertEquals("Bingo!", p.get(KEY_TO_BE_ADDED));
    }

    @Test
    public void emptyArrayPassesForbidden() throws Exception {
        builder.forbid(EMPTY_ARRAY_KEY);
        assertTrue(builder.problems.toString(), builder.isPacketValid());
    }

    @Test
    public void forbiddenFieldRejected() throws Exception {
        builder.forbid(NEED_KEY);
        assertFalse(builder.problems.toString(), builder.isPacketValid());
    }

    @Test
    public void interestingFieldsIdentified() throws Exception {
        builder.interestedIn(INTERESTING_KEY);
        Packet packet = builder.result();
        packet.put(INTERESTING_KEY, "@SallyThePlatinum");
        assertEquals("@SallyThePlatinum", packet.get(INTERESTING_KEY));
    }

    @Test
    public void renderingJson() throws Exception {
        String expected = SOLUTION_STRING.replace(":2", ":3"); // Update read_count
        assertJsonEquals(expected, builder.result().toJson());
    }

    @Test
    public void changedKeyJson() throws Exception {
        builder.require(NEED_KEY);
        builder.result().put(NEED_KEY, "airline_offer");
        String expected = SOLUTION_STRING
                .replace(":2", ":3")
                .replace("car_rental_offer", "airline_offer");
        assertJsonEquals(expected, builder.result().toJson());
    }

    @Test
    public void builderTraitChaining() throws Exception {
        builder
                .require(NEED_KEY)
                .forbid(EMPTY_ARRAY_KEY, KEY_TO_BE_ADDED)
                .interestedIn(INTERESTING_KEY);
        assertTrue(builder.isPacketValid());
    }

    @Test
    public void manipulatingJsonArrays() throws Exception {
        List solutions = builder
                .require(SOLUTIONS_KEY)
                .result()
                .getList(SOLUTIONS_KEY);
        assertEquals(3, solutions.size());
    }

    @Test
    public void requiredValue() throws Exception {
        builder.requireValue(NEED_KEY, "car_rental_offer");
        assertTrue(builder.isPacketValid());
        builder.requireValue("user_id", 666);
        assertFalse((builder.isPacketValid()));
    }

    @Test
    public void manipulateCollection() throws Exception {
        builder.require(SOLUTIONS_KEY);
        solutions = (Collection) builder.result().get(SOLUTIONS_KEY);
        assertEquals(3, solutions.size());
    }

    @Test
    public void readCountAddedByDefault() throws Exception {
        Packet nakedPacket = new PacketBuilder("{}").result();
        assertEquals(0.0, json(nakedPacket.toJson()).get(Packet.READ_COUNT));
    }

    private void assertJsonEquals(String expected, String actual) {
        assertEquals(json(expected), json(actual));
    }

    private Map json(String jsonString) {
        return new Gson().fromJson(jsonString, HashMap.class);
    }
}