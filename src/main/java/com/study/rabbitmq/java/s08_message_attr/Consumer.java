package com.study.rabbitmq.java.s08_message_attr;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 简单队列消费者
 *
 * @author Hash
 * @since 2020/10/17
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setUsername("admin");
        factory.setPassword("admin");

        String queueName = "queue8";

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("消费者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            // 定义收到消息后的回调
            DeliverCallback callback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println("收到消息：" + new String(message.getBody(), "UTF-8"));
                    System.out.println("ContentType = " + message.getProperties().getContentType());
                    System.out.println("ReplyTo = " + message.getProperties().getReplyTo());
                }
            };

            // 开启队列消费
            channel.basicConsume(queueName, true, callback, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                }
            });

            System.out.println("开始接收消息");
            System.in.read();
        }
    }
}
