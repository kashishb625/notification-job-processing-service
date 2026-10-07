package com.Kashish.notification_job_service.service;

import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@Service
public class NotificationJobMetrics 
{
	private final NotificationJobRepository notificationJobRepository;

	public NotificationJobMetrics(NotificationJobRepository notificationJobRepository) 
	{
		this.notificationJobRepository = notificationJobRepository;
	}
	
	public long getTotalJobs()
	{
		return notificationJobRepository.count();
	}
	
	public long getCompletedJobs()
	{
		return notificationJobRepository.countByStatus("COMPLETED");
	}
	
	public long getFailedJobs()
	{
		return notificationJobRepository.countByStatus("FAILED");
	}
	
	public long getRetryingJobs()
	{
		return notificationJobRepository.countByStatus("RETRYING");
	}

}
