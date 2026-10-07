package com.Kashish.notification_job_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Kashish.notification_job_service.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{
	
}
