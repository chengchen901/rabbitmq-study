package com.study.rabbitmq.java.s04_routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author Hash
 * @since 2020/10/3
 */
public class RoutingConsumer {
    private static final String EXCHANGE_NAME = "routing-sample";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
             Connection connection2 = factory.newConnection();
             Channel channel2 = connection2.createChannel();) {

            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "orange");

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            // 第二个消费者
            String queueName2 = channel2.queueDeclare().getQueue();
            channel2.queueBind(queueName2, EXCHANGE_NAME, "black");
            channel2.queueBind(queueName2, EXCHANGE_NAME, "green");
            channel2.basicConsume(queueName2, true, deliverCallback, consumerTag -> {});

            System.out.println("开始接收消息");
            System.in.read();
        }
    }
}
