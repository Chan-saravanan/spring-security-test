package com.app.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebApplicationSecurity extends WebSecurityConfigurerAdapter{
	
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public WebApplicationSecurity(PasswordEncoder passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http
		.authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/api/**").hasAnyRole(ApplicationUserRole.STUDENT.name())
//		.antMatchers(HttpMethod.POST,   "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//		.antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//		.antMatchers(HttpMethod.PATCH, "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//		.antMatchers("/management/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name(), ApplicationUserRole.ADMINTRAINEE.name())
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();
	}
	
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user1 = User
				.builder()
				.username("student")
				.password(passwordEncoder.encode("studentpassword"))
				//.roles(ApplicationUserRole.STUDENT.name())//ROLE_STUDENT!
				.authorities(ApplicationUserRole.STUDENT.getGrantedAuthority())
				.build();
		
		UserDetails user2 = User
				.builder()
				.username("admin1")
				.password(passwordEncoder.encode("admin1password"))
				//.roles(ApplicationUserRole.ADMIN.name())//ROLE_ADMIN!
				.authorities(ApplicationUserRole.ADMIN.getGrantedAuthority())
				.build();
		
		UserDetails user3 = User
				.builder()
				.username("admin2")
				.password(passwordEncoder.encode("admin2password"))
				//.roles(ApplicationUserRole.ADMINTRAINEE.name())//ROLE_ADMINTRAINEE!
				.authorities(ApplicationUserRole.ADMINTRAINEE.getGrantedAuthority())
				.build();
		
		return new InMemoryUserDetailsManager(user1, user2, user3);
	}
}