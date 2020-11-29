package com.study.rabbitmq.java.s17_cluster;

import com.rabbitmq.client.*;

/**
 * 简单队列消费者
 *
 * @author Hash
 * @since 2020/11/15
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 开启自动恢复
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        // 设置网络恢复间隔时间
        // factory.setNetworkRecoveryInterval(3000); // 默认是5秒
        // 集群多个可连接点
        Address[] addresses = { new Address("192.168.254.155"), new Address("192.168.254.156"), new Address("192.168.254.157") };

        String queueName = "queue-x-1";

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection(addresses, "消费者1");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();
                Channel channel2 = connection.createChannel();) {

            // 5 创建消息交换器
            channel.exchangeDeclare("my-exchange", "fanout", true, false, null);

            // 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
            channel.queueDeclare(queueName, true, false, false, null);

            channel.queueBind(queueName, "my-exchange", "");

            // 6、定义收到消息后的回调
            DeliverCallback callback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), "UTF-8") + " at " + System.currentTimeMillis() / 1000);
            };

            // 7、开启队列消费
            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });

            // 第二个消费者
            channel2.queueDeclare("queue-x-2", false, false, false, null);
            channel2.queueBind("queue-x-2", "my-exchange", "");
            channel2.basicConsume("queue-x-2", true, callback, consumerTag -> {
            });

            System.out.println("开始接收消息 at " + System.currentTimeMillis() / 1000);
            // 按任意键退出程序
            System.in.read();

        }
    }
}
