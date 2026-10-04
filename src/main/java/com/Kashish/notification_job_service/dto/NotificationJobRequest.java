package com.Kashish.notification_job_service.dto;

import jakarta.validation.constraints.NotBlank;

public class NotificationJobRequest 
{
	@NotBlank(message="Recipient name cannot be empty")
	private String recipient;
	@NotBlank(message="Message is required")
	private String message;
	
	public String getRecipient() 
	{
		return recipient;
	}
	
	public void setRecipient(String recipient) 
	{
		this.recipient = recipient;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message) 
	{
		this.message = message;
	}
	
	

}
