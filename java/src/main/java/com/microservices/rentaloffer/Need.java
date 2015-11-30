package com.microservices.rentaloffer;

import java.io.IOException;

public class Need {

    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];

        Need.publish(host, port);
    }

    public static void publish(String host, String port) {
        try (Connections connection = new Connections(host, port)) {
            while (true) {
                connection.publish(new NeedPacket().toJson());
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not publish message:", e);
        }
    }
}
