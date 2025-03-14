package com.chase.portfolio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configures security filters and authentication settings
	
	//public static String[] TestApiCalls = new String[] {"api/auth/login", "api/auth/refresh"};
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//http.addFilter(new RequestLoggingFilter());
		http.csrf((csrf) -> csrf
				//.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/**")) // Enable CSRF for API endpoints
	            //.ignoringRequestMatchers("/api/auth/login"));  // Disable CSRF for login endpoint only
				.disable());
		http.authorizeHttpRequests((authorize) -> 
			authorize
				.requestMatchers("/**").permitAll()
				//.requestMatchers(HttpMethod.GET, "/**").permitAll()
				//.requestMatchers(HttpMethod.POST, "api/auth/login", "api/auth/refresh").permitAll()
				.anyRequest().permitAll());
				//.anyRequest().authenticated());// Allow all preflight requests
	    //http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}
}