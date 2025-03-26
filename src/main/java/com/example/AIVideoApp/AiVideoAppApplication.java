// ✅ 올바른 `AiVideoAppApplication.java`
package com.example.AIVideoApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class})
public class AiVideoAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiVideoAppApplication.class, args);
    }
}
