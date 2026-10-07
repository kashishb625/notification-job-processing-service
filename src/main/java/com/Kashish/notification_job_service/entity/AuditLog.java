package com.Kashish.notification_job_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="audit_logs")
public class AuditLog 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Long jobId;
	private String username;
	private String event;
	private LocalDateTime timestamp;
	public Long getId()
	{
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public long getJobId() 
	{
		return jobId;
	}
	
	public void setJobId(Long jobId) 
	{
		this.jobId = jobId;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEvent()
	{
		return event;
	}
	
	public void setEvent(String event)
	{
		this.event = event;
	}
	
	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}
	
}
