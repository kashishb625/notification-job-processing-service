package com.Kashish.notification_job_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Kashish.notification_job_service.entity.NotificationJob;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, Long>
{
	long countByStatus(String status);

}
