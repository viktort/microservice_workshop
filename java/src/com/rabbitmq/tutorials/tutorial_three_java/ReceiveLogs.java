package com.rabbitmq.tutorials.tutorial_three_java;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private static final String RABBIT_MQ_PUB_SUB = "fanout";
    private static final String EXCHANGE_NAME = "rapids";
    private static final String QUEUE_NAME_BASE = "receive_logs_";

    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;
    private static String queueName;

    // Invoke with parameters: <ip_address> <port>
    public static void main(String[] argv) {
        validateArgs(argv);
        establishConnectivity(argv);
        configurePubSub();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        consumeMessages(consumer(channel));
    }

    private static void configurePubSub() {
        declareExchange();
        declareQueue();
        bindQueueToExchange();
    }

    private static void establishConnectivity(String[] argv) {
        factory = new ConnectionFactory();
        factory.setHost(argv[0]);                       // Set to IP address of RabbitMQ
        factory.setPort(Integer.parseInt(argv[1]));   // Set to port of RabbitMQ

        connection = connection();
        channel = channel();
    }

    private static void validateArgs(String[] argv) {
        if (argv.length < 2) {
            System.out.println("Start this service with <ip_address> <port> for RabbitMQ");
            throw new IllegalArgumentException("Start this service with <ip_address> <port> for RabbitMQ");
        }
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

    private static void bindQueueToExchange() {
        try {
            channel.queueBind(queueName, EXCHANGE_NAME, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException binding Queue to Exchange", e);
        }
    }

    private static AMQP.Queue.DeclareOk declareQueue() {
        try {
            queueName = QUEUE_NAME_BASE + UUID.randomUUID().toString();
            // Configured for non-durable, auto-delete, and exclusive
            return channel.queueDeclare(queueName, false, true, true, new HashMap<String, Object>());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException declaring Queue", e);
        }
    }

    private static void declareExchange() {
        try {
            // Configure for non-durable, auto-delete
            channel.exchangeDeclare(EXCHANGE_NAME, RABBIT_MQ_PUB_SUB, false, true, new HashMap<String, Object>());
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
