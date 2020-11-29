package com.study.rabbitmq.spring.s09_publisher_confirm.routing_return;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Hash
 * @since 2020/10/18
 */
@SpringBootApplication
@EnableScheduling // spring 中的定时功能，此处只是为了多次发送消息
public class RoutingReturn {

    @Bean
    public FanoutExchange hello() {
        return new FanoutExchange("spring-Routing-return");
    }

    @Bean
    public RabbitTemplate busiARabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true); // 设置消息不可以路由退回
        // 设置消息退回回调 【注意】一个 RabbitTemplate 只能设置一个 ReturnCallback
        template.setReturnCallback(myReturnCallback());
        return template;
    }

    private RabbitTemplate.ReturnCallback myReturnCallback() {
        return new RabbitTemplate.ReturnCallback() {
            @Override
            // replyCode broker的回应码 replyText 回应描述
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
                                        String routingKey) {

                // 在这里写退回处理逻辑
                System.out.println("收到回退消息 replyCode=" + replyCode + " replyText=" + replyText + " exchange=" + exchange
                        + " routingKey=" + routingKey);

                System.out.println(" 消息：" + message);
            }
        };
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private org.springframework.amqp.core.FanoutExchange FanoutExchange;

    @Scheduled(fixedDelay = 1000) // 定时多次发送消息
    public void send() {
        // this.template.setMandatory(true);
        String message = "Hello World!";
        this.template.convertAndSend(FanoutExchange.getName(), "routingkeyaaa", message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RoutingReturn.class, args);
        // 按任意键退出程序
        System.in.read();
    }
}
