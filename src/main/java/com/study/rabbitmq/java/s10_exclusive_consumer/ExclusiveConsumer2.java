package com.study.rabbitmq.java.s10_exclusive_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.TimeUnit;

/**
 * 间隔一定时间，不断重试来保证高可用。
 *
 * @author Hash
 * @since 2020/10/18
 */
public class ExclusiveConsumer2 {

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
                Connection connection = factory.newConnection("消费者1");) {

            // 4、从链接中创建通道
            Channel channel = connection.createChannel();

            // 6、定义收到消息后的回调
            DeliverCallback callback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), "UTF-8"));
            };

            boolean ok = false;
            while (!ok) {
                try {
                    // 7、注册独占消费者
                    channel.basicConsume(queueName, true, "consumer-2", false, true, null, callback, consumerTag -> {
                    });
                    ok = true;
                    System.out.println("恢复独占消费者");
                } catch (Exception e) {
                    System.out.println("注册独占消费者失败");
                    TimeUnit.SECONDS.sleep(3L); // 3秒后重试
                    // 抛出异常后channel会被关闭，所以在这里需要创建
                    channel = connection.createChannel();
                }
            }

            System.out.println("开始接收消息");
            // 按任意键退出程序
            System.in.read();
            channel.close();
        }
    }
}
