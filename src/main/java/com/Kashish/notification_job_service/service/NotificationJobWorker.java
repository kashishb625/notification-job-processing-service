package com.Kashish.notification_job_service.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.config.RabbitMQConfig;
import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@Service
public class NotificationJobWorker
{
	
	private final NotificationJobRepository notificationJobRepository;
	private final NotificationJobProducer notificationJobProducer;
	private final AuditLogService auditLogService;
	
    public NotificationJobWorker(NotificationJobRepository notificationJobRepository, NotificationJobProducer notificationJobProducer
    		,AuditLogService auditLogService) 
    {
		this.notificationJobRepository = notificationJobRepository;
		this.notificationJobProducer=notificationJobProducer;
		this.auditLogService=auditLogService;
	}


	@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processJob(Map<String, Object> message)
    {
		Long jobId=((Number) message.get("id")).longValue();
		
		NotificationJob job=notificationJobRepository.findById(jobId).orElseThrow(()->new RuntimeException("Notification job not found with id: "+jobId));
		
		job.setStatus("PROCESSING...");
		notificationJobRepository.save(job); 
		auditLogService.logEvent(job.getId(),null, "JOB_PROCESSING");
		
		int maxRetries=3;
		
		try 
		{
		System.out.println("=================================");
        System.out.println("Notification Job Received");
        System.out.println("Job ID: " + message.get("id"));
        System.out.println("Recipient: " + message.get("recipient"));
        System.out.println("Message: " + message.get("message"));
        System.out.println("Attempt: "+(job.getRetryCount()+1));
        
        System.out.println("Processing notification...");

        System.out.println("Notification processed successfully.");
        
        job.setStatus("COMPLETED");
        notificationJobRepository.save(job); 
        auditLogService.logEvent(job.getId(),null, "JOB_COMPLETED");
        
        System.out.println("Job Status: COMPLETED");
        System.out.println("=================================");
     
		}
		catch(Exception e)
		{
			int retryCount=job.getRetryCount()+1;
			job.setRetryCount(retryCount);
			
			if(retryCount>=maxRetries)
			{
				job.setStatus("FAILED");
				
				System.out.println("Job Permanently failed after "+retryCount+" attempts");
				
			    notificationJobRepository.save(job);
			    auditLogService.logEvent(job.getId(),null, "JOB_FAILED");
			}
			
			else
			{
				job.setStatus("RETRYING");
				
				notificationJobRepository.save(job);
				auditLogService.logEvent(job.getId(),null, "JOB_RETRYING");
				 
				System.out.println("Notification processing failed. "+"Retry attempt: "+retryCount);
				
				notificationJobProducer.sendJob(job);
			}
		
		}
		
		}
}