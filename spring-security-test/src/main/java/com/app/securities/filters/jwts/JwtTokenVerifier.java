package com.app.securities.filters.jwts;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenVerifier extends OncePerRequestFilter{
	private final JwtConfiguration jwtConfig;
	private final JwtSecretKey jwtSecretKey;
	
	public JwtTokenVerifier(JwtConfiguration jwtConfig, JwtSecretKey jwtSecretKey) {
		this.jwtSecretKey = jwtSecretKey;
		this.jwtConfig = jwtConfig;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
		
		if(Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer "))
		{
			try {
				String token  = authorizationHeader.replace(jwtConfig.getTokenPrefix(),"");
				Jws<Claims> claimsJws = Jwts
					.parserBuilder()
					.setSigningKey(jwtSecretKey.getSecretKey())
					.build()
					.parseClaimsJws(token);
				
				Claims body = claimsJws.getBody();
				
				String username = body.getSubject();
				
				logger.info("authorities :"+body.get("authorities"));
				List<Map<String, String>> authorities = (List<Map<String, String>>)body.get("authorities");
				logger.info("authorities list :"+authorities );
				
				Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream().map(entry->new SimpleGrantedAuthority(entry.get("authority"))).collect(Collectors.toSet());
				
				logger.info("simple granted authorities set :"+simpleGrantedAuthorities);
				
				Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}catch(JwtException e) {
				
				throw new IllegalStateException("Token cannot be trusted!");
			}
			filterChain.doFilter(request, response);
		}
		else
		{
			filterChain.doFilter(request, response);
			return;
		}
	}
	
//	private static class Map<T,V>
//	{
//		private String T;
//		
//		private String V;
//		
//		public Map() {
//			// TODO Auto-generated constructor stub
//		}
//
//		public String getT() {
//			return T;
//		}
//
//		public void setT(String t) {
//			T = t;
//		}
//
//		public String getV() {
//			return V;
//		}
//
//		public void setV(String v) {
//			V = v;
//		}
//
//		@Override
//		public String toString() {
//			return "Map [KEY=" + T + ", VALUE=" + V + "]";
//		}
//	}
}
