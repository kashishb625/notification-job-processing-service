package com.Kashish.notification_job_service.service;

import java.util.List;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.dto.NotificationJobRequest;
import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.exception.ResourceNotFoundException;
import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@Service
public class NotificationJobService 
{
	private final NotificationJobRepository notificationJobRepository;
	private final NotificationJobProducer notificationJobProducer;
	private final AuditLogService auditLogService;

	public NotificationJobService(NotificationJobRepository notificationJobRepository, 
			NotificationJobProducer notificationJobProducer, AuditLogService auditLogService) 
	{
		this.notificationJobRepository = notificationJobRepository;
		this.notificationJobProducer=notificationJobProducer;
		this.auditLogService=auditLogService;
	}
	
	public NotificationJob createJob(NotificationJobRequest request)
	{
		NotificationJob job=new NotificationJob();
		
		job.setRecipient(request.getRecipient());
		job.setMessage(request.getMessage());
		job.setStatus("PENDING");
		job.setRetryCount(0);
		
		
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		
		String username=authentication.getName();
		
		NotificationJob savedJob= notificationJobRepository.save(job);
		
		auditLogService.logEvent(savedJob.getId(), username, "JOB_CREATED");
		
		notificationJobProducer.sendJob(savedJob);

		return savedJob;
	}

	public NotificationJob getJobById(Long id)
	{
		return notificationJobRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification job not found with id: "+id));
	}
	
	public List<NotificationJob> getAllJobs()
	{
		return notificationJobRepository.findAll();
	}
	
}
