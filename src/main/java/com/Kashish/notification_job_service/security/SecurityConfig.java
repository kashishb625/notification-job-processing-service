package com.Kashish.notification_job_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig 
{
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			CustomAccessDeniedHandler customAccessDeniedHandler)
	{
	    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	    this.customAccessDeniedHandler=customAccessDeniedHandler;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http
		.csrf(csrf-> csrf.disable())
		.authorizeHttpRequests(auth->auth.
				requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/users/**").hasRole("ADMIN")
				.anyRequest().authenticated())
		.exceptionHandling(exception ->
	    exception.accessDeniedHandler(customAccessDeniedHandler))
		.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

}
