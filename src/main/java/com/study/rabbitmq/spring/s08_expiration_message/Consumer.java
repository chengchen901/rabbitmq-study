package com.study.rabbitmq.spring.s08_expiration_message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Hash
 * @since 2020/10/17
 */
@Component
public class Consumer {

    @RabbitListener(queues = "spring-queue8")
    public void receive(String in, @Headers Map<String, Object> headers, @Header("amqp_expiration") String expiration,
                        @Header("amqp_replyTo") String replyTo, @Header("contentType") String contentType) {
        System.out.println(" [x] Received '" + in + "'");
        System.out.println(headers);
        System.out.println(expiration);
        System.out.println(replyTo);
        System.out.println(contentType);
    }

}
