package com.study.rabbitmq.test.s01_message_priority;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Hash
 * @since 2020/10/3
 */
//@Component
public class HelloWorldConsumer {

    @RabbitListener(queues = "hello")
    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }
}
