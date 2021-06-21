package com.app.securities.filters.jwts;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private final static Logger logger= Logger.getLogger(JwtUsernamePasswordAuthenticationFilter.class.getName());
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtConfiguration jwtConfig;
	private final JwtSecretKey jwtSecretKey;
	
	public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfiguration jwtConfig, JwtSecretKey jwtSecretKey) {
		this.authenticationManager = authenticationManager;
		this.jwtSecretKey = jwtSecretKey;
		this.jwtConfig = jwtConfig;
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			UsernamePasswordAuthenticationRequest requestInfo = new ObjectMapper().readValue(request.getInputStream(), UsernamePasswordAuthenticationRequest.class);
			
			Authentication authentication = new UsernamePasswordAuthenticationToken(requestInfo.getUsername(), requestInfo.getPassword()); 
			
			Authentication authenticationResponse = authenticationManager.authenticate(authentication);
			
			return authenticationResponse;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		String token = Jwts
			.builder()
			.setSubject(authResult.getName())
			.claim("authorities", authResult.getAuthorities())
			.setIssuedAt(new java.util.Date())
			.setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
			.signWith(jwtSecretKey.getSecretKey())
			.compact();
		
		logger.info("user :"+authResult.getName());
		logger.info("Token :"+token);
		
		response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix().concat(token));
	}
}
