package com.Kashish.notification_job_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kashish.notification_job_service.dto.UserResponse;
import com.Kashish.notification_job_service.entity.User;
import com.Kashish.notification_job_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController 
{
	private final UserService userService;
	
	
	public UserController(UserService userService) 
	{
		this.userService = userService;
	}
	
	
	@GetMapping
	public ResponseEntity<List<UserResponse>> getUsers()
	{
		List<User> users=userService.getAllUsers();
		
		List<UserResponse> responses=users.stream().map(user->
		{
			UserResponse response=new UserResponse();
			response.setId(user.getId());
			response.setUsername(user.getUsername());
			response.setRole(user.getRole());
			
			return response;
			}
		).toList();
		
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id)
	{
		User user=userService.getUserById(id);
		UserResponse response=new UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setRole(user.getRole());
		
		return ResponseEntity.ok(response);
	}
	
	

}
