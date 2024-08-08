package org.example.jiraboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class JiraBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiraBoardApplication.class, args);
    }
}