package com.study.rabbitmq.java.s09_publisher_confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 批量发布确认
 *
 * @author Hash
 * @since 2020/10/18
 */
public class BatchConfirm {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.151");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setPort(5672);

        try (
                // 3、从连接工厂获取连接 //可以给连接命个名
                Connection connection = factory.newConnection("生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            // 声明exchange
            channel.exchangeDeclare("mandatory-ex", "fanout");

            /* 批量发布确认 *****************/
            // 1 开启发布确认模式
            channel.confirmSelect();

            // 2 发送一批消息
            for (int i = 1; i < 10; i++) {
                // 消息内容
                String message = "消息" + i;
                // 发送消息
                channel.basicPublish("mandatory-ex", "", false, null, message.getBytes());
                System.out.println("发布消息：" + message);
            }

            // 3 等待该批消息的确认结果
            boolean batchConfirmResult = channel.waitForConfirms();
            // 等待一定时间获取确认结果
            // boolean batchConfirmResult = channel.waitForConfirms(3000L);
            // 下面两个方法则是以抛出IOException表示失败（任意一个消息 nack 则抛出IOException）
            // channel.waitForConfirmsOrDie();
            // channel.waitForConfirmsOrDie(timeout);

            // 4得到确认结果后的后处理
            if (batchConfirmResult) {
                System.out.println("该批确认OK,继续进行下一批发布");
            } else {
                System.out.println("该批确认 NON OK,重发该批或做其他处理");
            }
        }
    }
}
