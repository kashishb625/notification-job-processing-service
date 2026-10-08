package com.Kashish.notification_job_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.Kashish.notification_job_service.dto.NotificationJobRequest;
import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.exception.ResourceNotFoundException;
import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationJobServiceTest 
{
	@InjectMocks
	private NotificationJobService notificationJobService;
	
	@Mock
	private NotificationJobRepository notificationJobRepository;
	
	@Mock
	private NotificationJobProducer notificationJobProducer;
	
	@Mock
	private AuditLogService auditLogService;
	
	@Test
	void getJobById_shouldReturnJob()
	{
		NotificationJob job=new NotificationJob();
		job.setId(1L);
		job.setRecipient("testuser@example.com");
		job.setMessage("Test Notification");
		job.setStatus("PENDING");
		job.setRetryCount(0);
		
		when(notificationJobRepository.findById(1L)).thenReturn(Optional.of(job));
		
		NotificationJob result=notificationJobService.getJobById(1L);
		
		assertNotNull(result);
		assertEquals(1L,result.getId());
		assertEquals("testuser@example.com", result.getRecipient());
		assertEquals("PENDING", result.getStatus());
		
		verify(notificationJobRepository).findById(1L);
	}
	
	@Test
	void getJobById_shouldThrowExceptionWhenJobNotFound()
	{
		when(notificationJobRepository.findById(99L)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, ()->notificationJobService.getJobById(99L));
		
		verify(notificationJobRepository).findById(99L);
	}
	
	@Test
	void createJob_shouldCreatePendingJobAndSendToQueue()
	{
		NotificationJobRequest request=new NotificationJobRequest();
		
		request.setRecipient("testUser@example.com");
		request.setMessage("Test Notification");
				
		NotificationJob savedJob=new NotificationJob();
		
		savedJob.setId(1L);
		savedJob.setRecipient("testUser@example.com");
		savedJob.setMessage("Test Notification");
		savedJob.setStatus("PENDING");
		savedJob.setRetryCount(0);
		
		when(notificationJobRepository.save(any(NotificationJob.class))).thenReturn(savedJob);
		
		try(var mockStatic=mockStatic(SecurityContextHolder.class))
		{
			Authentication authentication=mock(Authentication.class);
			
			when(authentication.getName()).thenReturn("testuser");
			
			mockStatic.when(SecurityContextHolder::getContext)
            .thenReturn(mock(SecurityContext.class));

    SecurityContext context =
            SecurityContextHolder.getContext();

    when(context.getAuthentication())
            .thenReturn(authentication);

    NotificationJob result =
            notificationJobService.createJob(request);

    assertNotNull(result);
    assertEquals("PENDING", result.getStatus());
    assertEquals(0, result.getRetryCount());

    verify(notificationJobRepository)
            .save(any(NotificationJob.class));

    verify(auditLogService)
            .logEvent(1L, "testuser", "JOB_CREATED");

    verify(notificationJobProducer)
            .sendJob(savedJob);
		}
	}
	

}
