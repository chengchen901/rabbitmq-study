package com.study.rabbitmq.spring.s01_helloworld;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Hash
 * @since 2020/10/3
 */
@Component
public class HelloWorldConsumer {

    @RabbitListener(queues = "hello")
    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }
}
