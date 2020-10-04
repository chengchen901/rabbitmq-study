package com.study.rabbitmq.java.s05_topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author Hash
 * @since 2020/10/3
 */
public class TopicConsumer {
    private static final String EXCHANGE_NAME = "topic-sample";

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

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();
            // 绑定的 routingKey 可有任意多个词以.相连,* 表示任意一个词，#表示任意多个词
            // routingKey的最大长度 255字节
            channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            // 第二个消费者
            String queueName2 = channel2.queueDeclare().getQueue();
            channel2.queueBind(queueName2, EXCHANGE_NAME, "*.*.rabbit");
            channel2.queueBind(queueName2, EXCHANGE_NAME, "lazy.#");
            channel2.basicConsume(queueName2, true, deliverCallback, consumerTag -> {
            });

            System.out.println("开始接收消息");
            System.in.read();
        }
    }
}
