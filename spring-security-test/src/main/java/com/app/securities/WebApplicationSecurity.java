package com.app.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.securities.filters.jwts.JwtConfiguration;
import com.app.securities.filters.jwts.JwtSecretKey;
import com.app.securities.filters.jwts.JwtTokenVerifier;
import com.app.securities.filters.jwts.JwtUsernamePasswordAuthenticationFilter;
import com.app.services.AuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebApplicationSecurity extends WebSecurityConfigurerAdapter{
	
	private PasswordEncoder passwordEncoder;
	private AuthenticationService authenticationService;
	private final JwtConfiguration jwtConfig;
	private final JwtSecretKey jwtSecretKey;

	
	@Autowired
	public WebApplicationSecurity(PasswordEncoder passwordEncoder, AuthenticationService authenticationService, JwtConfiguration jwtConfig, JwtSecretKey jwtSecretKey)
	{
		this.passwordEncoder = passwordEncoder;
		this.authenticationService = authenticationService;
		this.jwtSecretKey = jwtSecretKey;
		this.jwtConfig = jwtConfig;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtSecretKey))
			.addFilterAfter(new JwtTokenVerifier(jwtConfig, jwtSecretKey), JwtUsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers("/","/login","/**").permitAll()
			.antMatchers("/api/**").hasAnyRole(ApplicationUserRole.STUDENT.name())
			.anyRequest()
			.authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(doaAuthenticationProvider());
	}
	
	@Bean 
	public DaoAuthenticationProvider doaAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(authenticationService);
		return provider;
	}
}