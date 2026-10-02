package com.Kashish.notification_job_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Kashish.notification_job_service.entity.User;

public interface UserRepository extends JpaRepository<User, Long> 
{
	User findByUsername(String username);
}
