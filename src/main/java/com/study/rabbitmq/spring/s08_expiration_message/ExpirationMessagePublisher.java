package com.study.rabbitmq.spring.s08_expiration_message;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Hash
 * @since 2020/10/17
 */
@SpringBootApplication
@EnableScheduling // spring 中的定时功能，此处只是为了多次发送消息
public class ExpirationMessagePublisher {

    @Bean
    public Queue hello() {
        return new Queue("spring-queue8");
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    @Scheduled(fixedDelay = 1000) // 定时多次发送消息
    public void send() {
        String message = "Hello World!";
        this.template.convertAndSend(queue.getName(), message, mess -> {
            mess.getMessageProperties().setExpiration("10000"); // 设置消息的过期时间
            mess.getMessageProperties().setReplyTo("aaaaaaaaaaa");
            return mess;
        });
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExpirationMessagePublisher.class, args);
        // 按任意键退出程序
        System.in.read();
    }
}
