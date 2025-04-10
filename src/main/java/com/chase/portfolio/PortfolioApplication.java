package com.chase.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PortfolioApplication {
	
	

	public static void main(String[] args) {
		//http://localhost:8080
		EnvLoader.init();
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
