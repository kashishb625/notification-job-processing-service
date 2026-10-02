package com.Kashish.notification_job_service.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Kashish.notification_job_service.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService
{
	@Value("${jwt.secret}")
	private String secret;
	
	private SecretKey getSigningKey()
	{
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public String generateToken(User user)
	{
		return Jwts.builder()
				.subject(user.getUsername())
				.claim("role", user.getRole())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				.signWith(getSigningKey()).compact();
	}
	
	public String extractUsername(String token)
    {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token)
    {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token)
    {
        try
        {
            extractClaims(token);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private Claims extractClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

