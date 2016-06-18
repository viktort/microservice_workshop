package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.*;

// Understands the construction process for a JSON message packet
class PacketBuilder {
    private final String candidateJsonString;
    private Map<String, Object> jsonHash = new HashMap<>();
    private Packet packet;
    private final PacketProblems problems;

    public PacketBuilder(String jsonString, PacketProblems problems) {
        candidateJsonString = jsonString;
        this.problems = problems;
        try {
            Gson jsonEngine = new Gson();
            jsonHash = jsonEngine.fromJson(jsonString, HashMap.class);
            packet = new Packet(jsonHash);
        }
        catch(JsonSyntaxException e) {
            problems.severeError("Invalid JSON format per Gson library");
        }
        catch(Exception e) {
            problems.severeError("Unknown failure. Message is: " + e.toString());
        }
    }

    public Packet result() {
        if (problems.hasErrors()) throw new IllegalArgumentException(problems.toString());
        return packet;
    }

    public PacketBuilder require(String... requiredJsonKeys) {
        for (String key : requiredJsonKeys) {
            if (hasKey(key)) { registerAccessor(key); continue; }
            problems.error("Missing required key '" + key + "'");
        }
        return this;
    }

    public PacketBuilder forbid(String... forbiddenJsonKeys) {
        for (String key : forbiddenJsonKeys) {
            if (isKeyMissing(key)) {
                registerAccessor(key);
                continue;
            }
            problems.error("Forbidden key '" + key + "' already defined");
        }
        return this;
    }

    public PacketBuilder interestedIn(String... optionalKeys) {
        for (String key : optionalKeys) registerAccessor(key);
        return this;
    }

    public PacketBuilder requireValue(String requiredKey, Object requiredValue) {
        if (isKeyMissing(requiredKey) || !jsonHash.get(requiredKey).equals(requiredValue)) {
            problems.error("Required key '"
                    + requiredKey
                    + "' does not have required value '"
                    + requiredValue
                    + "'");
            return this;
        }
        registerAccessor(requiredKey);
        return this;
    }

    private void registerAccessor(String jsonKey) {
        packet.addAccessor(jsonKey);
    }

    private boolean hasKey(String requiredJsonKey) {
        // TODO: May need expansion for deeper keys...
        return jsonHash.containsKey(requiredJsonKey);
    }

    private boolean isKeyMissing(String forbiddenJsonKey) {
        // TODO: May need expansion for deeper keys...
        return !hasKey(forbiddenJsonKey) || isKeyEmptyArray(forbiddenJsonKey);
    }

    private boolean isKeyEmptyArray(String forbiddenJsonKey) {
        Object value = jsonHash.get(forbiddenJsonKey);
        if (!(value instanceof Collection)) return false;
        return ((Collection)value).isEmpty();
    }
}

