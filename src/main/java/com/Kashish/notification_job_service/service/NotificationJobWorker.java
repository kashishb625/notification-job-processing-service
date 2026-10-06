package com.Kashish.notification_job_service.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.config.RabbitMQConfig;

@Service
public class NotificationJobWorker
{
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processJob(Map<String, Object> message)
    {
        System.out.println("=================================");
        System.out.println("Notification Job Received");
        System.out.println("Job ID: " + message.get("id"));
        System.out.println("Recipient: " + message.get("recipient"));
        System.out.println("Message: " + message.get("message"));
        System.out.println("Status: " + message.get("status"));
        System.out.println("Retry Count: " + message.get("retryCount"));
        System.out.println("Processing notification...");
        System.out.println("Notification processed successfully.");
        System.out.println("=================================");
    }
}