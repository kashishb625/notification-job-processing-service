package com.Kashish.notification_job_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.config.RabbitMQConfig;
import com.Kashish.notification_job_service.entity.NotificationJob;

@Service
public class NotificationJobProducer
{
    private final RabbitTemplate rabbitTemplate;

    public NotificationJobProducer(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendJob(NotificationJob job)
    {
        java.util.Map<String, Object> message = new java.util.HashMap<>();

        message.put("id", job.getId());
        message.put("recipient", job.getRecipient());
        message.put("message", job.getMessage());
        message.put("status", job.getStatus());
        message.put("retryCount", job.getRetryCount());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}