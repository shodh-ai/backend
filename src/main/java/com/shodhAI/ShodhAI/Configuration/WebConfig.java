//package com.shodhAI.ShodhAI.Configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*") // Allow all origins or specify your frontend domain
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Include OPTIONS
//                .allowedHeaders("*") // Allow all headers
//                .allowCredentials(false) // Set to true if cookies are needed
//                .maxAge(3600); // Cache preflight response for 1 hour
//    }
//
//}
