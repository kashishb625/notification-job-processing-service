package com.Kashish.notification_job_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Kashish.notification_job_service.dto.LoginRequest;
import com.Kashish.notification_job_service.entity.User;
import com.Kashish.notification_job_service.service.JwtService;
import com.Kashish.notification_job_service.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) 
	{
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)
    {
        return new ResponseEntity<>(
                userService.createUser(user),
                HttpStatus.CREATED
        );
    }
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request)
	{
		User user=userService.findByUsername(request.getUsername());
		
		if(user==null || !passwordEncoder.matches(request.getPassword(), user.getPassword()))
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}
		String token=jwtService.generateToken(user);
		return ResponseEntity.ok(token);
	}
	
	
	

}
