package com.nrkei.microservices.rental_offer;

/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor implements MessageHandler {

    protected static Logger logger = LoggerFactory.getLogger(Monitor.class);

    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];

        Connections connection = new Connections(host, port);
        connection.deliveryLoop(new Monitor());
    }

    public void handle(String message) {
        logger.info(String.format(" [x] Received: %s", message));
    }

}
