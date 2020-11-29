package com.study.rabbitmq.java.s15_queue_length_limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 队列长度限制代码示例
 *
 * @author Hash
 * @since 2020/10/26
 */
public class Producer {

    public static void main(String[] argss) throws Exception {

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

            // 5、声明有长度限制的队列
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-max-length", 10); // 字节用 x-max-length-bytes
            args.put("x-overflow", "reject-publish"); // drop-head (default)

            String queueName = "queue-length-limit-1";

            channel.queueDeclare(queueName, false, false, false, args);

            // 清空队列 （必要时才做）
            channel.queuePurge(queueName);

            /* 流式发布确认 *****************/
            // 1 开启发布确认模式 就不能再做事务管理了
            channel.confirmSelect();
            // 2 待确认消息的Map
            Map<Long, String> messagesMap = new ConcurrentHashMap<>();

            // 3 指定流式确认事件回调处理
            channel.addConfirmListener((deliveryTag, multiple) -> { // multiple表示是否是多条的确认
                System.out.println("收到OK ack：deliveryTag=" + deliveryTag + " multiple=" + multiple + ",从Map中移除消息");
                // 从Map中移除对应的消息
                messagesMap.remove(deliveryTag);
            }, (deliveryTag, multiple) -> {
                System.out.println("收到 NON OK ack：deliveryTag=" + deliveryTag + " multiple=" + multiple + " 从Map中移除消息,重发或做其他处理");
                // 从Map中移除对应的消息
                String message = messagesMap.remove(deliveryTag);
                // 重发，或做其他处理
                System.out.println("失败消息：" + message);
            });

            for (int i = 1; i < 20; i++) {
                // 消息内容
                String message = "消息" + i;
                // 4 将消息放入到map中
                messagesMap.put(channel.getNextPublishSeqNo(), message);
                // 5、发送消息
                channel.basicPublish("", queueName, true, null, message.getBytes());
                System.out.println("发布消息：" + message);

                Thread.sleep(1000L);
            }
        }
    }
}
