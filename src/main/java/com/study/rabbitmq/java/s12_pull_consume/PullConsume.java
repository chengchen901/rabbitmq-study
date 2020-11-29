package com.study.rabbitmq.java.s12_pull_consume;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.util.concurrent.TimeUnit;

/**
 * @author Hash
 * @since 2020/10/25
 */
public class PullConsume {

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
                Channel channel = connection.createChannel();) {

            // 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
            channel.queueDeclare(queueName, true, false, false, null);

            System.out.println("开始接收消息");

            boolean autoAck = false;

            while (true) {
                // 从指定队列拉取一条消息
                GetResponse gr = channel.basicGet(queueName, autoAck);

                if (gr == null) {// 未取到消息
                    System.out.println("队列上没有消息");
                } else { // 取到消息
                    // 进行消息处理，然后根据处理结果决定该如何确认消息。
                    System.out.println("取得消息：" + new String(gr.getBody(), "UTF-8"));
                    System.out.println("消息的属性有：" + gr.getProps());
                    System.out.println("队列上的消息数量有：" + gr.getMessageCount());

                    // 获得消息传递标识
                    long deliveryTag = gr.getEnvelope().getDeliveryTag();
                    boolean multiple = false; // 是否批量
                    boolean requeue = true; // 是否重配送
                    // 手动单条确认消息ok
                    // channel.basicAck(deliveryTag, false);
                    // 手动单条确认消息reject，并重配送
                    // channel.basicReject(deliveryTag, requeue);
                    // 手动单条确认消息reject，移除不重配送
                    // channel.basicReject(deliveryTag, false);
                    // 手动单条确认消息Nack，并重配送
                    // channel.basicNack(deliveryTag, multiple, requeue);
                }
                TimeUnit.SECONDS.sleep(2L);
            }

        }
    }
}
