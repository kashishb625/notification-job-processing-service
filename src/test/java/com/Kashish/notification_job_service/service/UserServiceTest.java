package com.Kashish.notification_job_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Kashish.notification_job_service.entity.User;
import com.Kashish.notification_job_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest 
{
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Test
	void createUser_shouldEncodePasswordAndSetUserRole()
	{
		User user=new User();
		
		user.setUsername("testUser1");
		user.setPassword("testUser123");
		
		when(passwordEncoder.encode("testUser123")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		User result= userService.createUser(user);
		
		assertEquals("encodedPassword",result.getPassword());
		assertEquals("USER", user.getRole());
		
		verify(passwordEncoder).encode("testUser123");
		verify(userRepository).save(user);
		
	}
	
	@Test
	void findByUsername_shouldReturnUser()
	{
		
		User user=new User();
		user.setUsername("TestUser2");
		user.setPassword("userPass12");
		user.setRole("USER");
		
		when(userRepository.findByUsername("TestUser2")).thenReturn(user);
		
		User result=userService.findByUsername("TestUser2");
		
		assertNotNull(result);
		assertEquals("TestUser2", user.getUsername());
		assertEquals("userPass12", user.getPassword());
		assertEquals("USER", user.getRole());
		
		verify(userRepository).findByUsername("TestUser2");
	}

}
