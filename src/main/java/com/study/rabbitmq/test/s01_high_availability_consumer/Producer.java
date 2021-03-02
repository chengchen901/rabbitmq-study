package com.study.rabbitmq.test.s01_high_availability_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 简单队列生产者 使用RabbitMQ的默认交换器发送消息<br>
 * producer --> Queue --> Consumer
 *
 * @author Hash
 * @since 2020/10/3
 */
public class Producer {

    public static void main(String[] argv) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.155");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        // factory.setVirtualHost(virtualHost);

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel()) {

            /**
             * 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
             *
             * queueDeclare参数说明：
             *
             * @param queue
             *            队列名称
             * @param durable
             *            队列是否持久化
             * @param exclusive
             *            是否排他，即是否为私有的，如果为true,会对当前队列加锁，其它通道不能访问，并且在连接关闭时会自动删除，不受持久化和自动删除的属性控制
             * @param autoDelete
             *            是否自动删除，当最后一个消费者断开连接之后是否自动删除
             * @param arguments
             *            队列参数，设置队列的有效期、消息最大长度、队列中所有消息的生命周期等等
             */
            channel.queueDeclare("queue1", false, false, false, null);

            // 消息内容
            String message = "Hello World!";
            // 6、发送消息
            channel.basicPublish("", "queue1", null, message.getBytes());
            System.out.println("发送消息：" + message);

        }
    }
}
