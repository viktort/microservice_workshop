package com.rabbitmq.tutorials.tutorial_three_java;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class EmitLog {

    private static final String EXCHANGE_NAME = "rapids";
    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;

    // Invoke with parameters: <ip_address> <port> <optional_message>
    public static void main(String[] argv) {
        try {
            establishConnectivity(argv);
            configurePubSub();
            String message = message(argv);
            while (true) {
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("InterruptedException on sleep", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("UnsupportedEncodingException on message extraction", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose();
        }
    }

    private static void configurePubSub() {
        try {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");   // RabbitMQ terminology for pub/sub
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException on declaring exchange", e);
        }
    }

    private static void establishConnectivity(String[] argv) {
        factory = new ConnectionFactory();
        factory.setHost(argv[0]);                       // Set to IP address of RabbitMQ
        factory.setPort(Integer.parseInt(argv[1]));   // Set to port of RabbitMQ

        connection = connection();
        channel = channel();
    }

    private static void safeClose() {
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException on close", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException("TimeoutException on close", e);
        }
    }

    private static Channel channel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException:", e);
        }
    }

    private static Connection connection() {
        try {
            return factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException on creating Connection", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException("TimeoutException on creating Connection", e);
        }
    }

    private static String message(String[] strings){
        if (strings.length < 3) return "info: Hello World!";
        StringBuilder words = new StringBuilder(strings[2]);
        for (int i = 3; i < strings.length; i++) {
            words.append(" ").append(strings[i]);
        }
        return words.toString();
    }
}
