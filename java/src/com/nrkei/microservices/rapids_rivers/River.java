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
    private final List<Validation> validations = new ArrayList<>();

    public River(Rapids rapids) {
        this.rapids = rapids;
        rapids.register(this);
    }

    public void register(PacketListener listener) {
        listeners.add(listener);
    }

    @Override
    public void message(Rapids sendPort, String message) {
        PacketProblems problems = new PacketProblems(message);
        PacketBuilder builder = new PacketBuilder(message, problems);
        for (Validation v : validations) v.validate(builder);
        if (problems.hasErrors())
            onError(sendPort, problems);
        else
            packet(sendPort, builder);
    }

    private void packet(Rapids sendPort, PacketBuilder builder) {
        for (PacketListener l : listeners) l.packet(sendPort, builder.result());
    }

    private void onError(Rapids sendPort, PacketProblems problems) {
        for (PacketListener l : listeners) l.onError(sendPort, problems);
    }

    public void require(String jsonKey) {
        validations.add(new RequireKey(jsonKey));
    }

    public interface PacketListener {
        void packet(Rapids rapids, Packet packet);
        void onError(Rapids rapids, PacketProblems problems);
    }

    private interface Validation {
        void validate(PacketBuilder builder);
    }

    private class RequireKey implements Validation {
        private final String[] requiredKeys;

        RequireKey(String... requiredKeys) {
            this.requiredKeys = requiredKeys;
        }
        @Override
        public void validate(PacketBuilder builder) {
            builder.require(requiredKeys);
        }
    }

}
