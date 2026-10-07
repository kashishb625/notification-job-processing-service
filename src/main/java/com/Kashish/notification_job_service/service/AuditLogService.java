package com.Kashish.notification_job_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.entity.AuditLog;
import com.Kashish.notification_job_service.repository.AuditLogRepository;

@Service
public class AuditLogService
{
	private AuditLogRepository auditLogRepository;

	public AuditLogService(AuditLogRepository auditLogRepository) 
	{		
		this.auditLogRepository = auditLogRepository;
	}
	
	public void logEvent(Long jobId,String username,String event)
	{
		AuditLog auditLog=new AuditLog();
		
		auditLog.setJobId(jobId);
		auditLog.setUsername(username);
		auditLog.setEvent(event);
		auditLog.setTimestamp(LocalDateTime.now());
		
		auditLogRepository.save(auditLog);
	}
	

}
