package com.study.rabbitmq.java.s05_topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author Hash
 * @since 2020/10/3
 */
public class TopicProducer {
    private static final String EXCHANGE_NAME = "topic-sample";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        Random random = new Random();
        String[] speeds = { "higher", "middle", "lazy" };
        String[] colours = { "red", "orange", "blue", "black", "yellow", "green" };
        String[] species = { "pig", "rabbit", "monkey", "dog", "cat" };

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // 创建topic交换器
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String routingKey = null;
            for (int i = 0; i < 100; i++) {

                routingKey = speeds[random.nextInt(100) % speeds.length] + "."
                        + colours[random.nextInt(100) % colours.length] + "."
                        + species[random.nextInt(100) % species.length];

                String message = "message task-" + i + " routingKey=" + routingKey;
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("发送消息：" + message);
                Thread.sleep(3000L);
            }
        }
    }
}
