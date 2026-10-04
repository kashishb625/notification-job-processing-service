package com.Kashish.notification_job_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kashish.notification_job_service.dto.NotificationJobRequest;
import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.service.NotificationJobService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/jobs")
public class NotificationJobController 
{
	private final NotificationJobService notificationJobService;

	public NotificationJobController(NotificationJobService notificationJobService) 
	{
		this.notificationJobService = notificationJobService;
	}
	
	@PostMapping
	public ResponseEntity<NotificationJob> createJob(@Valid @RequestBody NotificationJobRequest request)
	{
		return new ResponseEntity<>(notificationJobService.createJob(request),
				HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<NotificationJob> getJobById(@PathVariable Long id)
	{
		return ResponseEntity.ok(notificationJobService.getJobById(id));
	}
	
	@GetMapping
	public ResponseEntity<List<NotificationJob>> getAllJobs()
	{
		return ResponseEntity.ok(notificationJobService.getAllJobs());
	}
	
	
}
