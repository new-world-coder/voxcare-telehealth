package com.voxcare.telehealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Telehealth Service
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TelehealthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelehealthServiceApplication.class, args);
    }
}
