package com.study.rabbitmq.java.s14_delay_message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Hash
 * @since 2020/10/25
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

            // 开启延时消息插件：
            // rabbitmq-plugins enable rabbitmq_delayed_message_exchange

            // 5 创建延时消息交换器
            Map<String, Object> argss = new HashMap<String, Object>();
            argss.put("x-delayed-type", "fanout"); // 保留有原来的交换类型，以支持消息路由
            channel.exchangeDeclare("my-exchange", "x-delayed-message", true, false, argss);

            // 6、发送消息
            AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
            HashMap<String, Object> headers = new HashMap<String, Object>();
            headers.put("x-delay", 5000); // 延时5秒
            props.headers(headers);

            for (int i = 0; i < 5; i++) {
                // 消息内容
                String message = "delay message task-" + System.currentTimeMillis() / 1000;

                channel.basicPublish("my-exchange", "", props.build(), message.getBytes());
                System.out.println("发送消息：" + message);
                TimeUnit.SECONDS.sleep(1L);
            }

        }
    }
}
