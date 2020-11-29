package com.study.rabbitmq.java.s10_exclusive_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * 简单队列消费者
 *
 * @author Hash
 * @since 2020/10/18
 */
public class ExclusiveConsumer {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        String queueName = "queue1";

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("消费者1");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();

                // 可以同一连接创建多个通道，也可是不同连接创建通道 来组成多个消费者
                // Connection connection2 = factory.newConnection("消费者2");
                // Channel channel2 = connection2.createChannel();
                Channel channel2 = connection.createChannel();) {

            // 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
            channel.queueDeclare(queueName, true, false, false, null);

            // 6、定义收到消息后的回调
            DeliverCallback callback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), "UTF-8"));
            };

            // 7、注册独占消费者
            channel.basicConsume(queueName, true, "consumer-1", false, true, null, callback, consumerTag -> {
            });

            // String basicConsume(String queue, boolean autoAck, String
            // consumerTag, boolean noLocal, boolean exclusive, Map<String,
            // Object> arguments, DeliverCallback deliverCallback,
            // CancelCallback cancelCallback) throws IOException; 还有几个重载方法。

            // 第二个消费者 注册不成功，将抛出异常。要做到高可用，就捕获异常不断重试
            // channel2.basicConsume(queueName, true, "consumer-2", false, true,
            // null, callback, consumerTag -> {
            // });

            System.out.println("开始接收消息");
            // 按任意键退出程序
            System.in.read();

        }
    }
}
