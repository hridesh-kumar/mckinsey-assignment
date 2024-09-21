package com.mckinsey.utilities;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.token.lifespan}")
	private int lifeSpan;

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + lifeSpan))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public Claims extractClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	public String getUserNameFromToken(String token) {
		return extractClaims(token).getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractClaims(token).getSubject();
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractClaims(token).getExpiration().before(new Date());
	}
}