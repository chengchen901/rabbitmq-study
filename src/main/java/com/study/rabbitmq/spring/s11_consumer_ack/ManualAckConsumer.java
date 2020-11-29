package com.study.rabbitmq.spring.s11_consumer_ack;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 消费者消息确认代码示例
 *
 * @author Hash
 * @since 2020/10/25
 */
@Component
public class ManualAckConsumer {

    @RabbitListener(queues = "hello") // direct 异步
    public void receive(String in, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        System.out.println(" [c] Received '" + in + "'");
        boolean multiple = false; // 是否批量
        boolean requeue = true; // 是否重配送
        // 手动单条确认消息ok
        channel.basicAck(deliveryTag, false);
        // 手动单条确认消息reject，并重配送
        // channel.basicReject(deliveryTag, requeue);
        // 手动单条确认消息reject，移除不重配送
        // channel.basicReject(deliveryTag, false);
        // 手动单条确认消息Nack，并重配送
        // channel.basicNack(deliveryTag, multiple, requeue);
    }
}
