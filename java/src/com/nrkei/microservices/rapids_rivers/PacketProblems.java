package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import java.util.ArrayList;
import java.util.List;

// Understands issue that arose when analyzing a JSON message
public class PacketProblems {

    private final String originalJson;
    private final List<String> informationMessages = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final List<String> severeErrors = new ArrayList<>();

    public PacketProblems(String originalJson) {
        this.originalJson = originalJson;
    }

    public boolean hasErrors() {
        return !(errors.isEmpty() && severeErrors.isEmpty());
    }

    private boolean hasMessages() {
        return !informationMessages.isEmpty() || !warnings.isEmpty() || hasErrors();
    }

    public void information(String explanation) {
        informationMessages.add(explanation);

    }
    public void warning(String explanation) {
        warnings.add(explanation);
    }

    public void error(String explanation) {
        errors.add(explanation);
    }

    public void severeError(String explanation) {
        severeErrors.add(explanation);
    }

    @Override
    public String toString() {
        if (!hasMessages()) return "No errors detected in JSON:\n\t" + originalJson;
        StringBuffer results = new StringBuffer();
        results.append("Messages exist. Original JSON string is:\n\t");
        results.append(originalJson);
        append("Severe errors", severeErrors, results);
        append("Errors", errors, results);
        append("Warnings", warnings, results);
        append("Information", informationMessages, results);
        results.append("\n");
        return results.toString();
    }

    private void append(String label, List<String> messages, StringBuffer results) {
        if (messages.isEmpty()) return;
        results.append("\n");
        results.append(label);
        results.append(": ");
        results.append(messages.size());
        for (String message : messages) {
            results.append("\n\t");
            results.append(message);
        }
    }
}
