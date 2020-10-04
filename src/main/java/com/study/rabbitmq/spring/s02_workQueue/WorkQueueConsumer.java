package com.study.rabbitmq.spring.s02_workQueue;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hash
 * @since 2020/10/3
 */
@SpringBootApplication
@EnableScheduling
public class WorkQueueConsumer {

    @RabbitListener(queues = "hello", concurrency = "2-5", containerFactory = "myFactory") // 并发2到5个消费者
    public void receive(Channel channel, String in) {
        System.out.println("Channel-" + channel.getChannelNumber() + " Received '" + in + "'");
    }

    @Bean
    public Queue hello() {
        return new Queue("hello");
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    AtomicInteger count = new AtomicInteger();

    @Scheduled(fixedDelay = 1000) // 定时多次发送消息
    public void send() {
        String message = "Hello World!" + count.incrementAndGet();
        this.template.convertAndSend(queue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WorkQueueConsumer.class, args);
        System.in.read();
    }
}
