package com.Kashish.notification_job_service.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.entity.User;
import com.Kashish.notification_job_service.exception.ResourceNotFoundException;
import com.Kashish.notification_job_service.repository.UserRepository;

@Service
public class UserService
{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordencoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordencoder) 
	{
		this.userRepository = userRepository;
		this.passwordencoder = passwordencoder;
	}
	
	public User createUser(User user)
	{
		user.setPassword(passwordencoder.encode(user.getPassword()));
		user.setRole("USER");
		
		return userRepository.save(user);
	}
	
	public User findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}
	
	public List<User> getAllUsers()
	{
	    return userRepository.findAll();
	}
	
	public User getUserById(Long id)
	{
		return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Does Not Exist with id: "+id));
	}
	

}
