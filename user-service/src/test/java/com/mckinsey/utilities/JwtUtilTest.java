package com.mckinsey.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
@TestPropertySource(locations = "classpath:test-application.properties")
class JwtUtilTest {

	@Autowired
    private JwtUtil jwtUtil;

	@Mock
	private UserDetails userDetails;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.token.lifespan}")
	private int lifeSpan;

	@Test
	void testGenerateToken() {
		userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
		String token = jwtUtil.generateToken(userDetails);
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

		assertEquals("testuser", claims.getSubject());
	}

	@Test
	void testExtractClaims() {
		String token = Jwts.builder().setSubject("testuser").setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + lifeSpan))
				.signWith(SignatureAlgorithm.HS256, secret).compact();

		Claims claims = jwtUtil.extractClaims(token);
		assertEquals("testuser", claims.getSubject());
	}

	@Test
	void testGetUserNameFromToken() {
		String token = Jwts.builder().setSubject("testuser").setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + lifeSpan))
				.signWith(SignatureAlgorithm.HS256, secret).compact();

		String username = jwtUtil.getUserNameFromToken(token);
		assertEquals("testuser", username);
	}

	@Test
	void testValidateToken() {
		userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
		String token = Jwts.builder().setSubject("testuser").setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + lifeSpan))
				.signWith(SignatureAlgorithm.HS256, secret).compact();

		assertTrue(jwtUtil.validateToken(token, userDetails));
	}

}
