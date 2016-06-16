package com.nrkei.microservices.rapids_rivers;

import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Ensures that PacketProblems operates correctly
public class PacketProblemsTest {

    private final static String VALID_JSON =
            "{\"key1\":\"value1\"}";

    @Test
    public void noProblemsFoundDefault() throws Exception {
        assertFalse(new PacketProblems(VALID_JSON).hasErrors());
    }
}