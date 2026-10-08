package com.Kashish.notification_job_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Kashish.notification_job_service.entity.NotificationJob;
import com.Kashish.notification_job_service.repository.NotificationJobRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationJobWorkerTest
{
    @Mock
    private NotificationJobRepository notificationJobRepository;

    @Mock
    private NotificationJobProducer notificationJobProducer;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private NotificationJobWorker notificationJobWorker;
    
    @Test
    void processJob_ShouldMarkJobAsCompleted()
    {
        NotificationJob job = new NotificationJob();

        job.setId(1L);
        job.setRecipient("test@example.com");
        job.setMessage("Test notification");
        job.setStatus("PENDING");
        job.setRetryCount(0);

        when(notificationJobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        Map<String, Object> message = Map.of(
                "id", 1L,
                "recipient", "test@example.com",
                "message", "Test notification"
        );

        notificationJobWorker.processJob(message);

        assertEquals("COMPLETED", job.getStatus());

        verify(notificationJobRepository, times(2))
                .save(job);

        verify(auditLogService)
                .logEvent(1L, null, "JOB_PROCESSING");

        verify(auditLogService)
                .logEvent(1L, null, "JOB_COMPLETED");

        verify(notificationJobProducer, never())
                .sendJob(job);
    }
    
    @Test
    void processJob_shouldRetryWhenProcessingFails()
    {
    	NotificationJob job=new NotificationJob();
    	job.setId(1L);
    	job.setRecipient("testUser@example.com");
    	job.setMessage("Test Notification");
    	job.setStatus("PENDING");
    	job.setRetryCount(0);
    	
    	when(notificationJobRepository.findById(1L)).thenReturn(Optional.of(job));
    	
    	when(notificationJobRepository.save(job))
        .thenReturn(job)
        .thenThrow(new RuntimeException("Processing Failed"))
        .thenReturn(job);
    	
    	Map<String,Object> message=Map.of("id",1L,
    			"recipient","testUser@example.com",
    			"message","Test Notification");
    	
    	notificationJobWorker.processJob(message);
    	
    	assertEquals(1,job.getRetryCount());
    	assertEquals("RETRYING", job.getStatus());
    	
    	verify(notificationJobProducer).sendJob(job);
    	verify(auditLogService).logEvent(1L, null, "JOB_RETRYING");
    }
    
    @Test
    void processJob_shouldMarkJobAsFailedAfterMaxRetries()
    {
        NotificationJob job = new NotificationJob();

        job.setId(1L);
        job.setRecipient("fail@example.com");
        job.setMessage("Test notification");
        job.setStatus("RETRYING");
        job.setRetryCount(2);

        when(notificationJobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(notificationJobRepository.save(job))
        .thenAnswer(invocation -> {
            if ("COMPLETED".equals(job.getStatus()))
            {
                throw new RuntimeException("Processing Failed");
            }
            return job;
        });
        
        Map<String, Object> message = Map.of(
                "id", 1L,
                "recipient", "fail@example.com",
                "message", "Test notification");

        notificationJobWorker.processJob(message);

        assertEquals(3, job.getRetryCount());
        assertEquals("FAILED", job.getStatus());

        verify(auditLogService).logEvent(1L, null, "JOB_FAILED");
        verify(notificationJobProducer, never()).sendJob(job);
    }
    
    
}
