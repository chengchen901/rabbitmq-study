package com.study.rabbitmq.spring.s06_rpc;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hash
 * @since 2020/10/4
 */
@Configuration
public class RpcConfiguration {

    @Bean
    public DirectExchange topic() {
        return new DirectExchange("spring.rpc");
    }

    @Configuration
    public static class ServerConfig {

        @Bean
        public Queue queue() {
            return new Queue("rpc.requests");
        }

        @Bean
        public Binding binding(DirectExchange exchange, Queue queue) {
            return BindingBuilder.bind(queue).to(exchange).with("rpc");
        }
    }
}
