package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Understands issue that arose when analyzing a JSON message
public class PacketProblems {

    private final String originalJson;

    public PacketProblems(String originalJson) {
        this.originalJson = originalJson;
    }

    public boolean hasErrors() {
        return false;
    }
}
