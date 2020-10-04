package com.study.rabbitmq.spring.s06_rpc;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Hash
 * @since 2020/10/4
 */
@SpringBootApplication
@EnableScheduling
public class SpringRpcServer {

    @RabbitListener(queues = "rpc.requests")
    // @SendTo("tut.rpc.replies") used when the client doesn't set replyTo.
    public int fibonacci(int n) {

        System.out.println(" [x] Received request for " + n);

        int result = fib(n);

        System.out.println(" [.] Returned " + result);

        return result;
    }

    public int fib(int n) {
        return n == 0 ? 0 : n == 1 ? 1 : (fib(n - 1) + fib(n - 2));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringRpcServer.class, args);
        System.in.read();
    }
}
