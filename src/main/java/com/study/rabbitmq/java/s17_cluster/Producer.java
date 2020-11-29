package com.study.rabbitmq.java.s17_cluster;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Hash
 * @since 2020/11/15
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 开启自动恢复
        factory.setAutomaticRecoveryEnabled(true);
        // 设置网络恢复间隔时间
        // factory.setNetworkRecoveryInterval(3000); // 默认是5秒
        // 集群多个可连接点
        Address[] addresses = { new Address("192.168.254.155"), new Address("192.168.254.156"), new Address("192.168.254.157") };

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection(addresses, "生产者");
                // 4、从链接中创建通道
                Channel channel = connection.createChannel();) {

            for (int i = 0; i < 100_100_100; i++) {
                // 消息内容
                String message = "message task" + i;
                try {
                    // 6、发送消息
                    channel.basicPublish("my-exchange", "", null, message.getBytes());
                } catch (Exception e) {
                    // i-- 是为了发送消息失败后重试发送消息
                    i--;
                    // 捕获连接失败异常，重新连接发送消息
                    e.printStackTrace();
                }
                System.out.println("发送消息：" + message);
                Thread.sleep(1000L);
            }

        }
    }
}
