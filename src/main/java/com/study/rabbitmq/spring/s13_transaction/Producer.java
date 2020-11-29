package com.study.rabbitmq.spring.s13_transaction;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author Hash
 * @since 2020/10/18
 */
@SpringBootApplication
public class Producer {

    @Bean
    public Queue hello() {
        return new Queue("hello");
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    @Transactional
    public void send(int i) {
        // 一定要设置ChannelTransacted(true) 表示开启通道事务
        this.template.setChannelTransacted(true);
        String message = "Hello World!-" + i;
        this.template.convertAndSend(queue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");
        if (i % 2 == 0)
            throw new RuntimeException();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(Producer.class, args);
        Producer prod = context.getBean(Producer.class);
        for (int i = 0; i < 10; i++) {
            try {
                prod.send(i);
            } catch (Exception e) {
                System.out.println("抛出异常了");
            }
            TimeUnit.SECONDS.sleep(1L);
        }
    }
}
