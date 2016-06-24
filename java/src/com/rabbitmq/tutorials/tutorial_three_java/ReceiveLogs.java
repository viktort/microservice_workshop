package com.rabbitmq.tutorials.tutorial_three_java;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "rapids";
    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;
    private static String queueName;

    // Invoke with parameters: <ip_address> <port>
    public static void main(String[] argv) {
        establishConnectivity(argv);
        configurePubSub();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        consumeMessages(consumer(channel));
    }

    private static void configurePubSub() {
        declareExchange();
        queueName = declareQueue().getQueue();
        bindQueueToExchange(queueName);
    }

    private static void establishConnectivity(String[] argv) {
        factory = new ConnectionFactory();
        factory.setHost(argv[0]);                       // Set to IP address of RabbitMQ
        factory.setPort(Integer.parseInt(argv[1]));   // Set to port of RabbitMQ

        connection = connection();
        channel = channel();
    }

    private static DefaultConsumer consumer(final Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
    }

    private static String consumeMessages(Consumer consumer) {
        try {
            return channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException while consuming messages", e);
        }
    }

    private static void bindQueueToExchange(String queueName) {
        try {
            channel.queueBind(queueName, EXCHANGE_NAME, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException binding Queue to Exchange", e);
        }
    }

    private static AMQP.Queue.DeclareOk declareQueue() {
        try {
            return channel.queueDeclare();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException declaring Queue", e);
        }
    }

    private static void declareExchange() {
        try {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException declaring Exchange", e);
        }
    }

    private static Channel channel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in creating Channel", e);
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
}
