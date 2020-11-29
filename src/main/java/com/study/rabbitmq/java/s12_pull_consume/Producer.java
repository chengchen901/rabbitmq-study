package com.study.rabbitmq.java.s12_pull_consume;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.util.concurrent.TimeUnit;

/**
 * @author Hash
 * @since 2020/10/25
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            String queueName = "my-queue-6";
            channel.queueDeclare(queueName, false, false, false, null);

            long start = System.currentTimeMillis();

            for (int i = 0; i < 100000000; i++) {
                // 消息内容
                String message = "message task" + i;
                // 6、发送消息
                channel.basicPublish("", queueName, null, message.getBytes());
                // System.out.println("发送消息：" + message);
                // Thread.sleep(1000L);
                if (i % 1000000 == 0) {
                    System.out.println(i + " 用时：" + (System.currentTimeMillis() - start));
                    start = System.currentTimeMillis();
                }
            }

        }
    }
}
