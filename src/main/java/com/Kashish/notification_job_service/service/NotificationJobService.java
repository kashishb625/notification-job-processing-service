package com.Kashish.notification_job_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.dto.NotificationJobRequest;
import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.exception.ResourceNotFoundException;
import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@Service
public class NotificationJobService 
{
	private final NotificationJobRepository notificationJobRepository;

	public NotificationJobService(NotificationJobRepository notificationJobRepository) 
	{
		this.notificationJobRepository = notificationJobRepository;
	}
	
	public NotificationJob createJob(NotificationJobRequest request)
	{
		NotificationJob job=new NotificationJob();
		
		job.setRecipient(request.getRecipient());
		job.setMessage(request.getMessage());
		job.setStatus("PENDING");
		job.setRetryCount(0);
		
		return notificationJobRepository.save(job);
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
