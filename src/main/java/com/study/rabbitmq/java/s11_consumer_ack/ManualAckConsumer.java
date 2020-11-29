package com.study.rabbitmq.java.s11_consumer_ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消费者消息确认代码示例
 *
 * @author Hash
 * @since 2020/10/25
 */
public class ManualAckConsumer {

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

            // 6、定义收到消息后的回调
            DeliverCallback callback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), "UTF-8"));

                // 进行消息处理，然后根据处理结果决定该如何确认消息。

                // 获得消息传递标识
                long deliveryTag = message.getEnvelope().getDeliveryTag();
                boolean multiple = false; // 是否批量
                boolean requeue = true; // 是否重配送
                // 手动单条确认消息ok
                // channel.basicAck(deliveryTag, false);
                // 手动单条确认消息reject，并重配送
                channel.basicReject(deliveryTag, requeue);
                // 手动单条确认消息reject，移除不重配送
                // channel.basicReject(deliveryTag, false);
                // 手动单条确认消息Nack，并重配送
                // channel.basicNack(deliveryTag, multiple, requeue);

            };

            // 设置一定的预取数量,当未确认数达到这个值时，broker将暂停配送消息给此消费者
            channel.basicQos(3);

            // 7、注册手动确认消费者
            channel.basicConsume(queueName, false, callback, consumerTag -> {
            });

            // String basicConsume(String queue, boolean autoAck,
            // DeliverCallback deliverCallback, CancelCallback cancelCallback)

            System.out.println("开始接收消息");
            // 按任意键退出程序
            System.in.read();

        }
    }
}
