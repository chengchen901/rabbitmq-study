package com.study.rabbitmq.java.s08_message_attr;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 非持久化和持久化带过期时间消息示例
 *
 * @author Hash
 * @since 2020/10/17
 */
public class PublisherSetAttribute {
    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (
                // 3、从连接工厂获取连接 //可以给连接命个名
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            channel.queueDeclare("queue8", true, false, false, null);

            // 消息内容
            String message = "非持久化消息";
            // 6、发送消息，经过测试deliveryMode默认为非持久化，持久化为2
            channel.basicPublish("", "queue8", new AMQP.BasicProperties.Builder().deliveryMode(1).build(),
                    message.getBytes());
            System.out.println("发送消息：" + message);

            message = "持久化带10s过期时间消息";
            channel.basicPublish("", "queue8", new AMQP.BasicProperties.Builder().expiration("10000").build(),
                    message.getBytes());
            System.out.println("发送消息：" + message);

            System.out.println("请到管理控制台看消息数量，10s后启动消费者程序看消费到哪条消息");
            System.out.println("再跑一遍，然后重启RabbitMQ，看非持久化消息的情况");
        }
    }
}
