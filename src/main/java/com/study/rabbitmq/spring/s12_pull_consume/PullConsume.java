package com.study.rabbitmq.spring.s12_pull_consume;

import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hash
 * @since 2020/10/25
 */
@SpringBootApplication
@EnableScheduling
public class PullConsume {

    @Autowired
    private RabbitTemplate template;

    AtomicInteger count = new AtomicInteger(0);

    @Scheduled(fixedDelay = 1000)
    public void send() {
        String message = "message-" + count.incrementAndGet();
        template.convertAndSend("", "queue1", message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    @Scheduled(fixedDelay = 1000)
    public void pull() throws Exception {

        /*Message message = template.receive("queue1");
        if (message == null) {
            System.out.println("队列为空");
        } else {
            System.out.println(" [c] get '" + new String(message.getBody(),
                    "UTF-8"));
        }*/

		// 如要手动确认
		this.template.execute(channel -> {
			// 从指定队列拉取一条消息
			GetResponse gr = channel.basicGet("queue1", false);

			if (gr == null) {// 未取到消息
				System.out.println("队列上没有消息");
			} else { // 取到消息
						// 进行消息处理，然后根据处理结果决定该如何确认消息。
				System.out.println("取得消息：" + new String(gr.getBody(), "UTF-8"));
				System.out.println("消息的属性有：" + gr.getProps());
				System.out.println("队列上的消息数量有：" + gr.getMessageCount());

				// 获得消息传递标识
				long deliveryTag = gr.getEnvelope().getDeliveryTag();
				boolean multiple = false; // 是否批量
				boolean requeue = true; // 是否重配送
				// 手动单条确认消息ok
				// channel.basicAck(deliveryTag, false);
				// 手动单条确认消息reject，并重配送
				channel.basicReject(deliveryTag, requeue);
				// 手动单条确认消息reject，移除不重配送
				// channel.basicReject(deliveryTag, false);
				// 手动单条确认消息Nack，并重配送
				// channel.basicNack(deliveryTag, multiple, requeue);
			}
			return null;
		});
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PullConsume.class, args);
        System.in.read();
    }
}
