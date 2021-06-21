package com.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.securities.ApplicationUserRole;
@Service
public class AuthenticationService implements UserDetailsService{
	private List<UserDetails> userList = new ArrayList<>();
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	@Autowired
	public AuthenticationService(PasswordEncoder passwordEncoder) {		
		UserDetails user1 = User
				.builder()
				.username("student")
				.password(passwordEncoder.encode("password"))
				.authorities(ApplicationUserRole.STUDENT.getGrantedAuthority())
				.build();
		
		UserDetails user2 = User
				.builder()
				.username("admin1")
				.password(passwordEncoder.encode("password"))
				.authorities(ApplicationUserRole.ADMIN.getGrantedAuthority())
				.build();
		
		UserDetails user3 = User
				.builder()
				.username("admin2")
				.password(passwordEncoder.encode("password"))
				.authorities(ApplicationUserRole.ADMINTRAINEE.getGrantedAuthority())
				.build();
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);
		
		logger.info("configured the userdetails informations!");
		logger.info(" user details list size : "+userList.size());
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Inside the load user by username for the user :"+username);
		
		UserDetails autheticatedUser = 
				userList
				.stream()
				.filter((user)-> user.getUsername().equals(username))
				.findFirst()
				.orElseThrow(
					()->new UsernameNotFoundException(String.format("username %s, not found!", username))
				);
		
		logger.info("Found the user!");
		return autheticatedUser;
	}
}
