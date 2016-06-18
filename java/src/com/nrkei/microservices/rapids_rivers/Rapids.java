package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import java.util.ArrayList;
import java.util.List;

// Understands an undistinguished stream of messages
public class Rapids {

    private final List<MessageListener> listeners = new ArrayList<>();


    public void register(MessageListener listener) {
        listeners.add(listener);
    }

    public interface MessageListener {
        void message(Rapids sendPort, String message);
    }
}
