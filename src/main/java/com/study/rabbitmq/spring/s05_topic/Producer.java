package com.study.rabbitmq.spring.s05_topic;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;
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
    private TopicExchange topic;

    AtomicInteger count = new AtomicInteger(0);

    Random random = new Random();
    String[] speeds = { "higher", "middle", "lazy" };
    String[] colours = { "red", "orange", "blue", "black", "yellow", "green" };
    String[] species = { "pig", "rabbit", "monkey", "dog", "cat" };

    @Scheduled(fixedDelay = 3000)
    public void send() {

        String routingKey = speeds[random.nextInt(100) % speeds.length] + "."
                + colours[random.nextInt(100) % colours.length] + "." + species[random.nextInt(100) % species.length];

        int i = count.incrementAndGet();

        String message = "topic message-" + i + " routingKey=" + routingKey;

        template.convertAndSend(topic.getName(), routingKey, message);

        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Producer.class, args);
        System.in.read();
    }
}
