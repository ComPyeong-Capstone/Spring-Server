// ✅ 올바른 `AiVideoAppApplication.java`
package com.example.AIVideoApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AiVideoAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiVideoAppApplication.class, args);
    }
}
