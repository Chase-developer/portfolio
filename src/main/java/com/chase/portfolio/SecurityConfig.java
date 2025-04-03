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
		//http.addFilter(new RequestLoggingFilter());
		/*
		 * .headers()
            .contentSecurityPolicy("default-src 'self'; "
                + "script-src 'self'; "
                + "img-src 'self' https://bucket-name.s3.us-ashburn-1.oraclecloud.com; "
                + "report-uri /csp-violation-report-endpoint;")
		 */
		http.headers((headers) -> 
			headers.contentSecurityPolicy((csp) -> 
				csp.policyDirectives("default-src 'self' https://objectstorage.eu-frankfurt-1.oraclecloud.com;"))
				);
		//This can be disabled safely for simple website without post, put, delete requests
		http.csrf((csrf) -> csrf
				//.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/**")) // Enable CSRF for API endpoints
	            //.ignoringRequestMatchers("/api/auth/login"));  // Disable CSRF for login endpoint only
				.disable());
		http.authorizeHttpRequests((authorize) -> 
			authorize
				//.requestMatchers(HttpMethod.GET, "/.well-known/pki-validation/E86F8C4B2F4DFDBADD3B43031B3C303D.txt").permitAll()
				.requestMatchers(HttpMethod.GET, "/", "/error", "/htb/**", "/home/**", "/favicon.ico").permitAll()
				
				.requestMatchers(HttpMethod.GET, StaticCalls).permitAll()
				//.requestMatchers("/actuator/**").permitAll()
				//.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
				//.requestMatchers("/**").permitAll());
				//.requestMatchers(HttpMethod.GET, "/**").permitAll()
				//.requestMatchers(HttpMethod.POST, "api/auth/login", "api/auth/refresh").permitAll()
				//.anyRequest().permitAll());
				.anyRequest().authenticated());// Allow all preflight requests
	    //http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}
}