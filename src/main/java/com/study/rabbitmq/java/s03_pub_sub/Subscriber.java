package com.study.rabbitmq.java.s03_pub_sub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author Hash
 * @since 2020/10/3
 */
public class Subscriber {
    private static final String EXCHANGE_NAME = "logs";

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

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            // 创建一个临时队列，名字自动生成（唯一），连接断开，自动删除
            String queueName = channel.queueDeclare().getQueue();
            // 将队列绑定到exchange,绑定时指定的routingKey 也称
            // 绑定Key,在fanout交换器中routingKey无用。
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            // 第二个消费者
            String queueName2 = channel2.queueDeclare().getQueue();
            channel2.queueBind(queueName2, EXCHANGE_NAME, "");
            channel2.basicConsume(queueName2, true, deliverCallback, consumerTag -> {});

            System.out.println("开始接收消息");
            System.in.read();
        }
    }
}
