package com.study.rabbitmq.java.s13_transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 事务发布消息示例
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
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setPort(5672);

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            // 创建队列
            String queueName = "queue1";
            channel.queueDeclare(queueName, true, false, false, null);

            // 开始事务模式
            channel.txSelect();

            for (int i = 0; i < 10; i++) {
                // 消息内容
                String message = "message task" + i;
                // 6、发送消息
                channel.basicPublish("", queueName, null, message.getBytes());
                System.out.println("发送消息：" + message);
                Thread.sleep(1000L);
            }

            // 提交事务
            channel.txCommit();
        }
    }
}
