package com.nrkei.microservices.rapids_rivers;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.google.gson.Gson;

import java.util.*;

// Understands a specific JSON-formatted message
public class Packet {

    final static String READ_COUNT = "system.read_count";

    private final Map<String, Object> jsonHash;
    private final Map<String, Object> recognizedKeys = new HashMap<>();

    Packet(Map<String, Object> jsonHash) {
        this.jsonHash = jsonHash;
        if (!jsonHash.containsKey(READ_COUNT)) jsonHash.put(READ_COUNT, -1.0);
        jsonHash.put(READ_COUNT, ((Double)jsonHash.get(READ_COUNT)).intValue() + 1);
    }

    void addAccessor(String requiredJsonKey) {
        if (!recognizedKeys.containsKey(requiredJsonKey))
            recognizedKeys.put(requiredJsonKey, jsonHash.get(requiredJsonKey));
    }

    public Object get(String key) {
        return recognizedKeys.get(key);
    }

    public void put(String key, Object value) {
        if (!recognizedKeys.containsKey(key))
            throw new IllegalArgumentException(
                    "Manipulated keys must be declared as required, forbidden, or interesting");
        recognizedKeys.put(key, value);
    }

    public String toJson() {
        Map<String, Object> updatedHash = new HashMap<>(jsonHash);
        for (String key : recognizedKeys.keySet())
            updatedHash.put(key, recognizedKeys.get(key));
        return new Gson().toJson(updatedHash);
    }

    public List getList(String solutionsKey) {
        return (List)get(solutionsKey);
    }
}

