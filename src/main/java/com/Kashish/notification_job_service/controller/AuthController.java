package com.Kashish.notification_job_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kashish.notification_job_service.dto.LoginRequest;
import com.Kashish.notification_job_service.entity.User;
import com.Kashish.notification_job_service.service.AuditLogService;
import com.Kashish.notification_job_service.service.JwtService;
import com.Kashish.notification_job_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuditLogService auditLogService;
	
	public AuthController(UserService userService, PasswordEncoder passwordEncoder,
			JwtService jwtService, AuditLogService auditLogService) 
	{
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.auditLogService=auditLogService;
	}
	
	@PostMapping("/register")
    public ResponseEntity<User> register(@Valid
    		@RequestBody User user)
    {
       User savedUser= userService.createUser(user);
       auditLogService.logEvent(null, savedUser.getUsername(), "USER_REGISTERED");
        
       return new ResponseEntity<>(savedUser,HttpStatus.CREATED);
    }
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request)
	{
	    User user = userService.findByUsername(request.getUsername());

	    if(user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword()))
	    {
	    	auditLogService.logEvent(null, user.getUsername(), "USER_LOGIN_FAILED");
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "Invalid username or password"));
	    }

	    String token = jwtService.generateToken(user);
	    
	    auditLogService.logEvent(null, user.getUsername(),"USER_LOGIN_SUCCESS");

	    return ResponseEntity.ok(Map.of("token", token));
	}
	
	
	

}
