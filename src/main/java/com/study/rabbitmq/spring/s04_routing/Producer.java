package com.study.rabbitmq.spring.s04_routing;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hash
 * @since 2020/10/4
 */
@SpringBootApplication
@EnableScheduling
public class Producer {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange direct;

    AtomicInteger count = new AtomicInteger(0);

    String[] routingKeys = { "orange", "black", "green" };

    @Scheduled(fixedDelay = 1000)
    public void send() {
        int i = count.incrementAndGet();
        String message = "routing message-" + i + " routingKey=" + routingKeys[i % 3];
        template.convertAndSend(direct.getName(), routingKeys[i % 3], message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Producer.class, args);
        System.in.read();
    }
}
