package com.chase.portfolio;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:8080")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization")  // Allow specific headers
        .allowCredentials(true);
        
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/fonts/**")
        		.addResourceLocations("classpath:/static/fonts/");
        registry.addResourceHandler("/texts/**")
				.addResourceLocations("classpath:/static/texts/");
    }
    
//    @Bean
//    public FilterRegistrationBean<ResponseInspectionFilter> loggingFilter() {
//        FilterRegistrationBean<ResponseInspectionFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new ResponseInspectionFilter());
//        //registrationBean.addUrlPatterns("/api/*");  // Only apply to specific URL patterns
//        return registrationBean;
//    }
}