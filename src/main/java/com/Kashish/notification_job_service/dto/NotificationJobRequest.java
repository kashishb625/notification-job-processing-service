package com.Kashish.notification_job_service.dto;

public class NotificationJobRequest 
{
	private String recipient;
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
