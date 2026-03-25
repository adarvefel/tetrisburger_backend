//package com.tetris.tetrisburger_backend.infrastructure.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Base64;
//
//@Configuration
//public class AlegraConfig {
//
//    @Value("${alegra.user}")
//    private String user;
//
//    @Value("${alegra.token}")
//    private String token;
//
//    @Value("${alegra.api-url}")
//    private String apiUrl;
//
//    @Bean
//    public RestTemplate alegraRestTemplate() {
//        return new RestTemplate();
//    }
//
//    public String getAuthHeader() {
//        String credentials = user + ":" + token;
//        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
//    }
//
//    public String getApiUrl() {
//        return apiUrl;
//    }
//}