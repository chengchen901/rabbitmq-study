package com.study.rabbitmq.spring.s03_pub_sub;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hash
 * @since 2020/10/3
 */
@SpringBootApplication
@EnableScheduling
public class Publisher {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private FanoutExchange fanout;

    AtomicInteger count = new AtomicInteger(0);

    @Scheduled(fixedDelay = 1000)
    public void send() {
        String message = "pub/sub message-" + count.incrementAndGet();
        template.convertAndSend(fanout.getName(), "", message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Publisher.class, args);
        System.in.read();
    }
}
