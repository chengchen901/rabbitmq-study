package com.study.rabbitmq.spring.s01_helloworld;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @EnableScheduling  spring 中的定时功能，此处只是为了多次发送消息
 * spring boot 中 amqp的使用说明：
 *  https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-messaging.html#boot-features-amqp
 *
 * @author Hash
 * @since 2020/10/3
 */
@SpringBootApplication
@EnableScheduling
public class HelloWorldProducer {

    /**
     * 【注意】这里配置一个我们要操作的Queue的bean,spring-rabbitmq框架在启动时将从容器中获取这些bean,
     * 并向rabbitmq服务器创建这些queue、exchange、binding。
     * 在RabbitAdmin.initialize()方法中做的这个事。
     * 其完成的工作为：channel.queueDeclare("hello",false, false, false, null);
     * 我们也可以自己手动通过 AmqpAdmin.declareXXX(xxx)方法来创建我们需要的queue、exchange、binding。
     *
     * @Autowired private AmqpAdmin amqpAdmin;
     *
     * public void send() {
     * 		...
     * 		this.amqpAdmin.declareQueue(new Queue("hello"));
     * 		...
     * }
     *
     * @return 队列实例
     */
    @Bean
    public Queue hello() {
        return new Queue("hello");
    }

    // @Autowired
    // private AmqpAdmin amqpAdmin;   //做queue、exchange、binding的管理用的

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    @Scheduled(fixedDelay = 1000)   //定时多次发送消息
    public void send() {
        String message = "Hello World!";
        this.template.convertAndSend(queue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HelloWorldProducer.class, args);
        System.in.read();
    }
}
