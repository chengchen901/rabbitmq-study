package com.study.rabbitmq.s09_publisher_confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.UnsupportedEncodingException;

/**
 * mandatory 示例，默认是false,消息不可以路由则会丢弃（或配置备用策略）；true 则返回给发布者
 *
 * @author Hash
 * @since 2020/10/17
 */
public class RoutingError {
    public static void main(String[] args) throws Exception {
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

            // 声明exchange,没有队列绑定到它
            channel.exchangeDeclare("mandatory-ex", "fanout");

            // 设置返回消息的回调处理
            channel.addReturnListener(returnMessage -> {
                try {
                    System.out.println("收到退回消息：" + new String(returnMessage.getBody(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            // 消息内容
            String message = "一条消息";
            // 6、发送消息
            channel.basicPublish("mandatory-ex", "", true, null, message.getBytes());
            System.out.println("发送消息：" + message);

            // 按任意键退出程序
            System.in.read();
        }
    }
}
