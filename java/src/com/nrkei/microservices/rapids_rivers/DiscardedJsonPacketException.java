package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import java.util.List;

// Understands that a message does not conform to expectations
public class DiscardedJsonPacketException extends IllegalArgumentException {

    public DiscardedJsonPacketException(String candidateJsonString, List<String> problems) {
        super("JSON packet failed validation. Cause(s) are:\n\t" + problems.toString());
    }

}
