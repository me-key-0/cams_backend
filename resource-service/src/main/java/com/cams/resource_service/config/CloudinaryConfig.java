package com.cams.resource_service.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Cloudinary integration.
 * Sets up the Cloudinary bean with credentials and configuration.
 */
@Configuration
public class CloudinaryConfig {
    
    private final String CLOUD_NAME = "cams";
    private final String API_KEY = "983326189595982";
    private final String API_SECRET = "R0fPg4Zi4beP9EIA0YRES3fRaTk";

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        
        return new Cloudinary(config);
    }
} 