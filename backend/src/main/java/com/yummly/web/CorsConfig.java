package com.yummly.web;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply CORS for all endpoints
                        .allowedOrigins("http://localhost:3002") // Allow React frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific methods
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // Allow specific headers
                        .allowCredentials(true)
                        .maxAge(3600); // Cache the pre-flight response for 1 hour

            }
        };
    }
}
