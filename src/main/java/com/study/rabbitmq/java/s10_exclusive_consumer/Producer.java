package com.study.rabbitmq.java.s10_exclusive_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 简单队列生产者
 *
 * @author Hash
 * @since 2020/10/18
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

            String queueName = "queue1";
            // 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
            channel.queueDeclare(queueName, true, false, false, null);

            for (int i = 0; i < 100; i++) {
                // 消息内容
                String message = "message task" + i;
                // 6、发送消息
                channel.basicPublish("", queueName, null, message.getBytes());
                System.out.println("发送消息：" + message);
                Thread.sleep(1000L);
            }

        }
    }
}
