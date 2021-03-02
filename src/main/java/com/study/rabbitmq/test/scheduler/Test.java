package com.study.rabbitmq.test.scheduler;

import com.study.rabbitmq.spring.s01_helloworld.HelloWorldProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Hash
 * @since 2021/1/27
 */
@SpringBootApplication
public class Test {

    @Scheduled(cron = "*/1 * * * * ? ")
    public void test1() {
        System.out.println(Thread.currentThread().getName() + "开始执行任务");
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "结束执行任务");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HelloWorldProducer.class, args);
        System.in.read();
    }
}
