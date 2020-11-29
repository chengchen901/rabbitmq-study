package com.study.rabbitmq.spring.s09_publisher_confirm.correlation_data_confirm;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * 单条确认示例
 *
 * @author Hash
 * @since 2020/10/18
 */
@SpringBootApplication
@EnableScheduling // spring 中的定时功能，此处只是为了多次发送消息
public class CorrelationDataConfirm {

    @Bean
    public org.springframework.amqp.core.FanoutExchange hello() {
        return new FanoutExchange("spring-Routing-return");
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private FanoutExchange FanoutExchange;

    @Scheduled(fixedDelay = 1000) // 定时多次发送消息
    public void send() throws Exception {
        CorrelationData cd1 = new CorrelationData();
        String message = "Hello World!";
        this.template.convertAndSend(FanoutExchange.getName(), "routingkeyaaa", message, cd1);
        System.out.println(" [x] Sent '" + message + "'");
        System.out.println("等待确认结果：" + cd1.getFuture().get(10, TimeUnit.SECONDS).isAck());
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CorrelationDataConfirm.class, args);
        // 按任意键退出程序
        System.in.read();
    }
}
