package com.myproject.getajob.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull; // Explicit import

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**") // sadece api altındakiyollara bu kuralı uygula
                .allowedOrigins("http://localhost:3000") // sadece react geliştirme sunucusundan gelen isteklere izin
                                                         // verecek
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // izin verilen metotlar
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(
            @NonNull org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        // Map /resumes/** URL to the local uploads/resumes/ directory
        // "file:uploads/resumes/" ensures it looks in the project root's uploads folder
        registry.addResourceHandler("/resumes/**")
                .addResourceLocations("file:uploads/resumes/");
    }

}
