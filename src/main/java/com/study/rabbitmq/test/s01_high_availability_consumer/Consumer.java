package com.study.rabbitmq.test.s01_high_availability_consumer;

import com.rabbitmq.client.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 简单队列消费者
 *
 * @author Hash
 * @since 2020/10/3
 */
public class Consumer {

    public static void main(String[] argv) throws Exception {
        // 1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接属性
        factory.setHost("192.168.254.155");
        factory.setUsername("admin");
        factory.setPassword("admin");

        String queueName = "hello";

        try (
                // 3、从连接工厂获取连接
                Connection connection = factory.newConnection("消费者");
                ) {

            // 4、从链接中创建通道
            Channel channel = connection.createChannel();

            /**
             * 5、声明（创建）队列 如果队列不存在，才会创建 RabbitMQ 不允许声明两个队列名相同，属性不同的队列，否则会报错
             *
             * queueDeclare参数说明：
             *
             * @param queue
             *            队列名称
             * @param durable
             *            队列是否持久化
             * @param exclusive
             *            是否排他，即是否为私有的，如果为true,会对当前队列加锁，其它通道不能访问，
             *            并且在连接关闭时会自动删除，不受持久化和自动删除的属性控制。 一般在队列和交换器绑定时使用
             * @param autoDelete
             *            是否自动删除，当最后一个消费者断开连接之后是否自动删除
             * @param arguments
             *            队列参数，设置队列的有效期、消息最大长度、队列中所有消息的生命周期等等
             */
            channel.queueDeclare(queueName, true, false, false, null);

            // 6、定义收到消息后的回调
            Channel finalChannel = channel;
            DeliverCallback callback = (consumerTag, message) -> {
                System.out.println(consumerTag + " 收到消息：" + new String(message.getBody(), "UTF-8"));

                // 进行消息处理，然后根据处理结果决定该如何确认消息。

                // 获得消息传递标识
                long deliveryTag = message.getEnvelope().getDeliveryTag();
                boolean multiple = false; // 是否批量
                boolean requeue = true; // 是否重配送
                // 手动单条确认消息ok
                finalChannel.basicAck(deliveryTag, false);
                // 手动单条确认消息reject，并重配送
                // channel.basicReject(deliveryTag, requeue);
                // 手动单条确认消息reject，移除不重配送
                // channel.basicReject(deliveryTag, false);
                // 手动单条确认消息Nack，并重配送
                // channel.basicNack(deliveryTag, multiple, requeue);

            };

            // 7、开启队列消费
            AtomicBoolean ok = new AtomicBoolean(false);
            while (true) {
                try {
                    if (ok.get()) {
                        TimeUnit.SECONDS.sleep(3L); // 3秒后重试
                        continue;
                    }

                    Channel finalChannel1 = channel;
                    channel.basicConsume(queueName, false, "高可用消费者", callback, (consumerTag)->{
                        System.out.println("consumerTag=" + consumerTag);
                        finalChannel1.queueDeclare(queueName, true, false, false, null);
                        ok.set(false);
                    });
                    ok.set(true);
                    System.out.println("消费者订阅成功");
                } catch (Exception e) {
                    System.out.println("注册消费者失败");
                    TimeUnit.SECONDS.sleep(3L); // 3秒后重试
                    // 抛出异常后channel会被关闭，所以在这里需要创建
                    channel = connection.createChannel();
                }
            }


//            System.out.println("开始接收消息");
//            System.in.read();

        }
    }
}
