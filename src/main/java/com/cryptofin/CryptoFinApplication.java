package com.cryptofin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoFinApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoFinApplication.class, args);
    }
}
