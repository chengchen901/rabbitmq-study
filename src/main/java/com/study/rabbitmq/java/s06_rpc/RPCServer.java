package com.study.rabbitmq.java.s06_rpc;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

/**
 * @author Hash
 * @since 2020/10/3
 */
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {

        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            return 1;
        }

        return fib(n - 1) + fib(n - 2);

    }

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.254.151");
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(delivery.getProperties().getCorrelationId()).build();

                String response = "";

                try {

                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                    int n = Integer.parseInt(message);

                    System.out.println(" [.] fib(" + message + ")");

                    response += fib(n);

                } catch (RuntimeException e) {

                    System.out.println(" [.] " + e.toString());

                } finally {

                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps,
                            response.getBytes(StandardCharsets.UTF_8));

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));

            // 按任意键退出程序
            System.in.read();
        }
    }
}
