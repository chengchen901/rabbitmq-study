package com.study.rabbitmq.java.s07_exchange_attr;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 验证Exchange的持久化、自动删除的示例
 *
 * @author Hash
 * @since 2020/10/17
 */
public class ExchangeAttribute {
    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setPort(5672);
        factory.setUsername("admin"); // 默认使用 “guest"
        factory.setPassword("admin"); // 默认使用 ”guest"
        // factory.setVirtualHost(virtualHost); // 不指定则连接到 "/" vhost

        try (
                // 3、从连接工厂获取连接 //可以给连接命个名
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            // 声明创建一个非持久化的，非自动删除的exchange
            channel.exchangeDeclare("exchange-test-nonDurable-nonAutodelete", "direct");
            // 声明一个持久化、非自动删除的exchange
            channel.exchangeDeclare("exchange-test-durable-nonAutodelete", "fanout", true);

            // 声明一个持久化、自动删除的的exchange
            channel.exchangeDeclare("exchange-test-durable-autodelete", "fanout", true, true, null);

            // 声明一个持久化、自动删除的的exchange
            channel.exchangeDeclare("exchange-test-nonDurable-autodelete", "fanout", false, true, null);

            // 创建一个临时队列
            String q1 = channel.queueDeclare().getQueue();
            // 绑定到两个自动删除的exchange上
            channel.queueBind(q1, "exchange-test-nonDurable-autodelete", "");
            channel.queueBind(q1, "exchange-test-durable-autodelete", "");

            // 按任意键退出程序
            System.in.read();

            // 去管理控制台看exchange列表
            // 然后按任意键退出程序，再去管理控制台看exchange列表，看自动删除的会删除没
            // 然后在重启rabbitmq服务，看非持久化的exchange还在吗
        }
    }
}
