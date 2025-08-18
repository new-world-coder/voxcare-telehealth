package com.pulsecare.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Config Service Application
 * 
 * This service provides centralized configuration management for all PulseCare microservices.
 * Services can fetch their configuration from this server at startup and runtime.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
