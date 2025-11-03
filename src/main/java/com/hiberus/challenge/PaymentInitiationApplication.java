package com.hiberus.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main application class for Payment Initiation Microservice.
 * This application migrates legacy SOAP services to REST API following BIAN standards.
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class PaymentInitiationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentInitiationApplication.class, args);
    }
}
