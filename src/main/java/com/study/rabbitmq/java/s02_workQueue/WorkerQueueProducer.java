package com.study.rabbitmq.java.s02_workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 简单队列生产者 使用RabbitMQ的默认交换器发送消息<br>
 * producer --> Queue --> 多个Consumer
 *
 * @author Hash
 * @since 2020/10/3
 */
public class WorkerQueueProducer {

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

            // 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
            channel.queueDeclare("queue1", false, false, false, null);

            for (int i = 0; i < 100; i++) {
                // 消息内容
                String message = "message task" + i;
                // 6、发送消息
                channel.basicPublish("", "queue1", null, message.getBytes());
                System.out.println("发送消息：" + message);
                Thread.sleep(1000L);
            }
        }
    }
}
