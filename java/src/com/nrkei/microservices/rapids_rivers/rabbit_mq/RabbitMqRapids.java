package com.nrkei.microservices.rapids_rivers.rabbit_mq;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.rabbitmq.client.*;
// TODO: Consider just using default Java console logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

// Understands an event bus implemented with RabbitMQ in pub/sub mode (fanout)
public class RabbitMqRapids extends RapidsConnection implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private final Channel channel;
    private final QueueingConsumer consumer;
    private final String amqpUrl;
    private final String queue = "";
    private final String exchange = "rapids";
    private final String exchangeType = "fanout";
    private final String routingKey;
    private final Connection connection;
    private AMQP.BasicProperties basicProperties;

    public RabbitMqRapids(String host, String port) {
        this.amqpUrl = amqpUrl(host, port);
        ConnectionFactory factory = factory();
        this.connection = connection(factory);
        this.basicProperties = new AMQP.BasicProperties().builder().build();
        this.channel = channel(connection);
        this.routingKey = queue;  // Assumes queue and routingKey are the same.
        declareExchange();
        this.consumer = consumer(channel, queue(channel));
        bindQueueToExchange(channel);
    }

    public void connect() {
        logger.info(String.format(" [*] Waiting for solutions on the %s bus... To exit press CTRL+C", amqpUrl));
        while (true) {
            final QueueingConsumer.Delivery delivery = delivery(consumer);
            if (delivery != null) {
                for (MessageListener listener : listeners) {
                    try {
                        listener.message(this, message(delivery));
                        ack(channel, delivery);
                    } catch (Exception ex) {
                        nack(channel, delivery);
                    }
                }
            }
        }
    }

    public void publish(String message) {
        try {
            // Assume that queue and routingKey are the same, as in other parts of Pika
            channel.basicPublish(exchange, queue, basicProperties, message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Could not publish message:", e);
        }
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not close connection:", e);
        }
    }

    protected Map<String, Object> headers(QueueingConsumer.Delivery delivery) {
        AMQP.BasicProperties properties = delivery.getProperties();
        return properties.getHeaders();
    }

    protected String amqpUrl(String host, String port) {
        return String.format("amqp://guest:guest@%s:%s", host, port);
    }

    protected String message(QueueingConsumer.Delivery delivery) {
        try {
            return new String(delivery.getBody(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to parse message:", e);
        }
    }

    protected void nack(Channel channel, QueueingConsumer.Delivery delivery) {
        try {
            long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            channel.basicNack(deliveryTag, false, false);
            logger.warn(String.format("Rejected message: tag: %d body: %s ", deliveryTag, new String(delivery.getBody())));
        } catch (IOException e) {
            throw new RuntimeException("Failed to nack delivery:", e);
        }
    }

    protected void ack(Channel channel, QueueingConsumer.Delivery delivery) {
        try {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (IOException e) {
            throw new RuntimeException("Failed to ack delivery:", e);
        }
    }

    protected QueueingConsumer.Delivery delivery(QueueingConsumer consumer) {
        try {
            return consumer.nextDelivery();
        } catch (InterruptedException e) {
            throw new RuntimeException("Consumer interrupted:", e);
        }
    }

    protected QueueingConsumer consumer(Channel channel, String queueName) {
        try {
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, false, consumer);
            return consumer;
        } catch (IOException e) {
            throw new RuntimeException("Could not create consumer:", e);
        }
    }

    protected void bindQueueToExchange(Channel channel) {
        try {
            channel.queueBind(queue, exchange, routingKey);
        } catch (IOException e) {
            throw new RuntimeException("Could not bind queue to exchange:", e);
        }
    }

    protected String queue(Channel channel) {
        try {
            channel.queueDeclare(queue, false, true, false, new HashMap<String, Object>());
            return queue;
        } catch (IOException e) {
            throw new RuntimeException("Could not declare queue:", e);
        }
    }

    protected void declareExchange() {
        try {
            channel.exchangeDeclare(exchange, exchangeType, true);
        } catch (Exception e) {
            throw new RuntimeException("Could not declare exchange:", e);
        }
    }

    protected Channel channel(Connection connection) {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            throw new RuntimeException("Could not create channel:", e);
        }
    }

    protected Connection connection(ConnectionFactory factory) {
        try {
            return factory.newConnection();
        } catch (IOException e) {
            throw new RuntimeException("Could not create channel:", e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Time out in creating channel: ", e);
        }
    }

    protected ConnectionFactory factory() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(amqpUrl);
            logger.info(amqpUrl);
            return factory;
        } catch (Exception ex) {
            String message = String.format("Failed to initialize ConnectionFactory with %s.", amqpUrl);
            throw new RuntimeException(message, ex);
        }
    }
}
