package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

// Understands a stream of valid JSON packets meeting certain criteria
// Implements GOF Observer pattern
// Implements GOF Command pattern
public class River implements Rapids.MessageListener {

    private final Rapids rapids;
    private final List<PacketListener> listeners = new ArrayList<>();

    public River(Rapids rapids) {
        this.rapids = rapids;
        rapids.register(this);
    }

    public void register(PacketListener listener) {
        listeners.add(listener);
    }

    @Override
    public void message(Rapids sendPort, String message) {
        for (PacketListener l : listeners) {

        }
    }

    public interface PacketListener {
        void packet(Rapids rapids, Packet packet);
        void onError(Rapids rapids, PacketProblems problems);
    }

}
