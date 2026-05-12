package com.camel.clinic.config;

import com.camel.clinic.util.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.camel.clinic.util.RabbitMQConstants.*;
import static com.camel.clinic.util.RabbitMQConstants.Exchange;

/**
 * RabbitMQ Configuration
 * <p>
 * Topology:
 * <p>
 * [Producer]
 * │
 * ▼
 * clinic.notification.exchange  (Direct)
 * ├── routing: clinic.email  → clinic.email.queue  → [EmailConsumer]
 * └── routing: clinic.push   → clinic.push.queue   → [PushConsumer]
 * <p>
 * clinic.task.exchange          (Direct)
 * └── routing: clinic.task   → clinic.task.queue   → [TaskConsumer]
 * <p>
 * clinic.pubsub.exchange        (Direct)
 * └── routing: clinic.pubsub → clinic.pubsub.queue → [PubSubConsumer]
 * <p>
 * clinic.dlx.exchange           (Dead Letter Exchange)
 * ├── clinic.email.dead      → clinic.email.dlq
 * ├── clinic.push.dead       → clinic.push.dlq
 * └── clinic.task.dead       → clinic.task.dlq
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        return factory;
    }


    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(Exchange.DEAD_LETTER)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return ExchangeBuilder
                .directExchange(Exchange.NOTIFICATION)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange taskExchange() {
        return ExchangeBuilder
                .directExchange(Exchange.TASK)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange pubsubExchange() {
        return ExchangeBuilder
                .directExchange(Exchange.PUBSUB)
                .durable(true)
                .build();
    }


    private Map<String, Object> dlxArgs(String deadRoutingKey) {
        return Map.of(
                "x-dead-letter-exchange", Exchange.DEAD_LETTER,
                "x-dead-letter-routing-key", deadRoutingKey,
                "x-message-ttl", DEFAULT_TTL_MS
        );
    }

    @Bean
    public org.springframework.amqp.core.Queue emailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.EMAIL)
                .withArguments(dlxArgs(RoutingKey.EMAIL_DEAD))
                .build();
    }

    @Bean
    public org.springframework.amqp.core.Queue pushQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.PUSH_NOTIFICATION)
                .withArguments(dlxArgs(RoutingKey.PUSH_DEAD))
                .build();
    }

    @Bean
    public org.springframework.amqp.core.Queue taskQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.TASK_BACKGROUND)
                .withArguments(dlxArgs(RoutingKey.TASK_DEAD))
                .build();
    }

    @Bean
    public org.springframework.amqp.core.Queue pubsubQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.PUBSUB_BROADCAST)
                .build();
    }

    @Bean
    public org.springframework.amqp.core.Queue emailDlq() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.EMAIL_DLQ).build();
    }

    @Bean
    public org.springframework.amqp.core.Queue pushDlq() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.PUSH_DLQ).build();
    }

    @Bean
    public org.springframework.amqp.core.Queue taskDlq() {
        return QueueBuilder.durable(RabbitMQConstants.Queue.TASK_DLQ).build();
    }


    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(RoutingKey.EMAIL);
    }

    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue())
                .to(notificationExchange())
                .with(RoutingKey.PUSH_NOTIFICATION);
    }

    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue())
                .to(taskExchange())
                .with(RoutingKey.TASK_BACKGROUND);
    }

    @Bean
    public Binding pubsubBinding() {
        return BindingBuilder.bind(pubsubQueue())
                .to(pubsubExchange())
                .with(RoutingKey.PUBSUB_BROADCAST);
    }

    // DLQ Bindings
    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq())
                .to(deadLetterExchange())
                .with(RoutingKey.EMAIL_DEAD);
    }

    @Bean
    public Binding pushDlqBinding() {
        return BindingBuilder.bind(pushDlq())
                .to(deadLetterExchange())
                .with(RoutingKey.PUSH_DEAD);
    }

    @Bean
    public Binding taskDlqBinding() {
        return BindingBuilder.bind(taskDlq())
                .to(deadLetterExchange())
                .with(RoutingKey.TASK_DEAD);
    }
}