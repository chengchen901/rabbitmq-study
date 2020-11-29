package com.study.rabbitmq.spring.s14_delay_message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Hash
 * @since 2020/10/25
 */
@Component
public class Consumer {

    @RabbitListener(queues = "delay-queue")
    public void receive2(String in) {
        System.out.println(" Received：" + in + " at " + System.currentTimeMillis() / 1000);
    }
}
