package com.nrkei.microservices.rapids_rivers;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

// Ensures that PacketProblems operates correctly
public class PacketProblemsTest {

    private final static String VALID_JSON = "{\"key1\":\"value1\"}";
    
    private PacketProblems problems;

    @Before
    public void setUp() throws Exception {
        problems = new PacketProblems(VALID_JSON);
    }

    @Test
    public void noProblemsFoundDefault() throws Exception {
        assertFalse(problems.hasErrors());
    }

    @Test
    public void errorsDetected() throws Exception {
        problems.error("Simple error");
        assertTrue(problems.hasErrors());
        assertThat(problems.toString(), containsString("Simple error"));
    }

    @Test
    public void severeErrorsDetected() throws Exception {
        problems.severeError("Severe error");
        assertTrue(problems.hasErrors());
        assertThat(problems.toString(), containsString("Severe error"));
    }

    @Test
    public void warningsDetected() throws Exception {
        problems.warning("Warning explanation");
        assertFalse(problems.hasErrors());
        assertThat(problems.toString(), containsString("Warning explanation"));
    }

    @Test
    public void informationalMessagesDetected() throws Exception {
        problems.warning("Information only message");
        assertFalse(problems.hasErrors());
        assertThat(problems.toString(), containsString("Information only message"));
    }
}