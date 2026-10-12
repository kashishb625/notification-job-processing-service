package com.Kashish.notification_job_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Kashish.notification_job_service.entity.NotificationJob;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, Long>
{
	long countByStatus(String status);
	
	 List<NotificationJob> findByCreatedBy(String createdBy);

	    Optional<NotificationJob> findByIdAndCreatedBy(
	            Long id, String createdBy);

}
