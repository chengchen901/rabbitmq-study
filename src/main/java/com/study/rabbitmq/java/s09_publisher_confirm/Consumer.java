package com.study.rabbitmq.java.s09_publisher_confirm;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 简单队列消费者
 *
 * @author Hash
 * @since 2020/10/18
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        String queueName = "unrouted";

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("消费者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {
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
             *            是否排他，即是否为私有的，如果为true,会对当前队列加锁，其它通道不能访问，
             *            并且在连接关闭时会自动删除，不受持久化和自动删除的属性控制。 一般在队列和交换器绑定时使用
             * @param autoDelete
             *            是否自动删除，当最后一个消费者断开连接之后是否自动删除
             * @param arguments
             *            队列参数，设置队列的有效期、消息最大长度、队列中所有消息的生命周期等等
             */
            channel.queueDeclare(queueName, true, false, false, null);

            // 6、定义收到消息后的回调
            DeliverCallback callback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println("收到消息：" + new String(message.getBody(), "UTF-8"));
                }
            };

            // 7、开启队列消费
            String consumerTag = channel.basicConsume(queueName, true, callback, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                }
            });

            System.out.println("开始接收消息");
            System.in.read();

        }
    }
}
