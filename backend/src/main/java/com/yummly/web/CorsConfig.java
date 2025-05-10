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
                .allowedOrigins(
                    "http://localhost:3000", 
                    "http://localhost:3001", 
                    "http://localhost:3002", 
                    "http://localhost:3003", 
                    "http://localhost:3004", 
                    "http://localhost:3005", 
                    "http://localhost:3006"
                ) 
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific methods
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // Allow specific headers
                        .allowCredentials(true)
                        .maxAge(3600); // Cache the pre-flight response for 1 hour

            }
        };
    }
}
