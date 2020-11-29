package com.study.rabbitmq.java.s16_flow_control;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 流控代码示例，在启动程序后可以去mq控制台connection中查看连接状态的变化
 *
 * @author Hash
 * @since 2020/10/26
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

            for (int i = 0; i < 100_000_000; i++) {
                // 消息内容
                String message = "message task" + i;
                // 6、发送消息
                channel.basicPublish("", "queue1", null, message.getBytes());
                // System.out.println("发送消息：" + message);
                // Thread.sleep(1000L);
            }

        }
    }
}
