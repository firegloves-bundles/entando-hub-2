package com.entando.hub.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${ALLOWED_API_ORIGIN}")
    private String allowedApiOrigin;

    @Value("${ALLOWED_APPBUILDER_ORIGIN}")
    private String allowedAppBuilderOrigin;

    @Bean
    public WebMvcConfigurer getCorsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/appbuilder/**")
                        .allowedOrigins(allowedAppBuilderOrigin)
                        .allowedMethods("*")
                        .allowedHeaders("*");
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedApiOrigin)
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}