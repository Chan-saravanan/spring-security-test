package com.app.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.services.AuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebApplicationSecurity extends WebSecurityConfigurerAdapter{
	
	private PasswordEncoder passwordEncoder;
	private AuthenticationService authenticationService;
	@Autowired
	public WebApplicationSecurity(PasswordEncoder passwordEncoder, AuthenticationService authenticationService)
	{
		this.passwordEncoder = passwordEncoder;
		this.authenticationService = authenticationService;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http
		.authorizeRequests()
		.antMatchers("/").permitAll()
		//.antMatchers("/api/**").hasAnyRole(ApplicationUserRole.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();
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