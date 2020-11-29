package com.study.rabbitmq.spring.s10_exclusive_consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Hash
 * @since 2020/10/18
 */
@Component
public class ExclusiveConsumer {

    @RabbitListener(queues = "hello", exclusive = true) // 【注意】独占时不能开并发
    public void receive(String in) {
        System.out.println(" [c1] Received '" + in + "'");
    }

    @RabbitListener(queues = "hello", exclusive = true)
    public void receive2(String in) {
        System.out.println(" [c2] Received '" + in + "'");
    }

}
