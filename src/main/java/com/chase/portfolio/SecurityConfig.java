package com.chase.portfolio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configures security filters and authentication settings
	
	//public static String[] TestApiCalls = new String[] {"api/auth/login", "api/auth/refresh"};
	
	public static final String[] StaticCalls = new String[] {"/css/**", "/js/**", "/images/**", "/fonts/**", "/texts/**", "/videos/**"};
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.headers((headers) -> 
			headers.contentSecurityPolicy((csp) -> 
				csp.policyDirectives("default-src 'self' https://objectstorage.eu-frankfurt-1.oraclecloud.com https://cdnjs.cloudflare.com;"))
				);
		//This can be disabled safely for simple website without post, put, delete requests. should be re-enabled when features are added tho
		http.csrf((csrf) -> csrf
				.disable());
		http.authorizeHttpRequests((authorize) -> 
			authorize
				.requestMatchers(HttpMethod.GET, "/", "/error", "/htb/**", "/home/**", "/favicon.ico", "/journey/**", "/views/**").permitAll()
				.requestMatchers(HttpMethod.GET, StaticCalls).permitAll()
				.anyRequest().authenticated());// Allow all preflight requests
	    return http.build();
	}
}