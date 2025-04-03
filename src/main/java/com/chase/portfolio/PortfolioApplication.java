package com.chase.portfolio;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.chase.portfolio.services.ResourceService;

@SpringBootApplication
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PortfolioApplication {
	
	public static boolean isInProject()
	{
		return new File(".project").exists();
	}

	public static void main(String[] args) {
		//http://localhost:8080
		if (PortfolioApplication.isInProject())
    		ResourceService.updateStaticIndex();
		EnvLoader.init();
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
