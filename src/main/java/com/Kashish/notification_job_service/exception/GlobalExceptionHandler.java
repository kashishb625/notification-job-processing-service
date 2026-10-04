package com.Kashish.notification_job_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex)
	{
		Map<String,Object> response=new HashMap<>();
		
		response.put("status", 400);
		response.put("message", "VALIDATION FAILED");
		
		Map<String,String> errors=new HashMap<>();
		
		ex.getBindingResult().getFieldErrors().forEach(error-> errors.put(error.getField(),error.getDefaultMessage()));
		
		response.put("errors", errors);
		
		return new ResponseEntity<>(
				response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex)
	{
		Map<String,Object> response=new HashMap<>();
		
		response.put("status", 400);
		response.put("message", ex.getMessage());
		
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}

}
