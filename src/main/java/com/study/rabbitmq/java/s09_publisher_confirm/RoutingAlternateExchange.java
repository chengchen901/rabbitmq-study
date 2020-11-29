package com.study.rabbitmq.java.s09_publisher_confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由不可达设置死信队列（备用队列）示例
 *
 * @author Hash
 * @since 2020/10/18
 */
public class RoutingAlternateExchange {

    public static void main(String[] argss) throws Exception {

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

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("alternate-exchange", "my-ae"); // 备用交换参数指定
            channel.exchangeDeclare("my-direct", "direct", false, false, args);
            channel.exchangeDeclare("my-ae", "fanout");
            channel.queueDeclare("routed", true, false, false, null);
            channel.queueBind("routed", "my-direct", "key1");
            channel.queueDeclare("unrouted", true, false, false, null);
            channel.queueBind("unrouted", "my-ae", "");
            // 消息内容
            String message = "一条消息";
            // 6、发送消息
            channel.basicPublish("my-direct", "key1", false, null, message.getBytes());
            System.out.println("发送消息：" + message);

            channel.basicPublish("my-direct", "key2", false, null, message.getBytes());

            // 按任意键退出程序
            System.in.read();
        }

    }

}
