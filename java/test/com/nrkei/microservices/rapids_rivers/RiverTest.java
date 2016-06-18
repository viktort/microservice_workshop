package com.nrkei.microservices.rapids_rivers;

import org.junit.Before;
import org.junit.Test;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.*;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Ensures that River triggers its RiverListeners correctly
public class RiverTest {

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


    private TestRapids rapids;
    private River river;

    @Before
    public void setUp() throws Exception {
        rapids = new TestRapids();
        river = new River(rapids);
    }

    @Test
    public void unconstrainedPacket() throws Exception {
        river.register(new TestPacketListener () {
            @Override
            public void packet(Rapids rapids, Packet packet) {
                assertNull(packet.get("need"));
            }
        });
        rapids.process(SOLUTION_STRING);
    }

    @Test
    public void requiredFieldExists() throws Exception {
        river.require("need");
        river.register(new TestPacketListener() {
            @Override
            public void packet(Rapids rapids, Packet packet) {
                assertEquals("car_rental_offer", packet.get("need"));
            }
        });
        rapids.process(SOLUTION_STRING);
    }

    private class TestRapids extends Rapids {

        void process(String message) {
            for (MessageListener l : listeners) l.message(this, message);
        }
    }

    private abstract class TestPacketListener implements River.PacketListener {
        @Override
        public void packet(Rapids rapids, Packet packet) {
            fail("Unexpected success parsing JSON packet. Packet is:\n" + packet.toJson());
        }

        @Override
        public void onError(Rapids rapids, PacketProblems problems) {
            fail("Unexpected JSON packet problem(s):\n" + problems.toString());
        }
    }
}