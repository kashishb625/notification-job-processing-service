package com.Kashish.notification_job_service.config;

import org.springframework.amqp.core.Binding;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitMQConfig
{
    public static final String QUEUE_NAME = "notification.queue";
    public static final String EXCHANGE_NAME = "notification.exchange";
    public static final String ROUTING_KEY = "notification.created";

    @Bean
    public Queue notificationQueue()
    {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange notificationExchange()
    {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding notificationBinding(
            Queue notificationQueue,
            DirectExchange notificationExchange)
    {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY);
    }
    
    @Bean
    public JacksonJsonMessageConverter messageConverter()
    {
        return new JacksonJsonMessageConverter();
    }
}