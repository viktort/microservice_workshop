package com.nrkei.microservices.car_rental_offer;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

// Understands the messages on an event bus
public class Monitor implements River.PacketListener {

    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];

        final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_all", host, port);
        final River river = new River(rapidsConnection);
        river.register(new Monitor());
        rapidsConnection.register(river);
    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
        System.out.println(String.format(" [>] %s", warnings));
    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
        System.out.println(String.format(" [>] %s", errors));
    }
}
