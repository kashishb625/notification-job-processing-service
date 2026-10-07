package com.Kashish.notification_job_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kashish.notification_job_service.service.NotificationJobMetrics;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController 
{
	private final NotificationJobMetrics notificationJobMetrics;

	public MetricsController(NotificationJobMetrics notificationJobMetrics) 
	{
		this.notificationJobMetrics = notificationJobMetrics;
	}
	
	@GetMapping("/jobs")
	public ResponseEntity<Map<String, Long>> getJobMetrics()
	{
		return ResponseEntity.ok (Map.of(
                "totalJobs",
                notificationJobMetrics.getTotalJobs(),

                "completedJobs",
                notificationJobMetrics.getCompletedJobs(),

                "failedJobs",
                notificationJobMetrics.getFailedJobs(),

                "retryingJobs",
                notificationJobMetrics.getRetryingJobs()));	
	}
	
	

}
