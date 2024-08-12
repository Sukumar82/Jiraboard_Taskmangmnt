package org.example.jiraboard.controller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
public class Testing {



        public static void main(String[] args) {
            // Create an instance of the BCryptPasswordEncoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Encode a plain text password
            String plainPassword = "sample22";
//            String encodedPassword = passwordEncoder.encode(plainPassword);
            String encodedPassword ="$2a$10$ReaS1dReHAPLhGZKcqF13eXwUwp8HHtBcEZEpFAV3GqqmRNCmSmu6";
            // Print the encoded password
            System.out.println("Encoded password: " + encodedPassword);

            // Check if the plain password matches the encoded password
            boolean isPasswordMatch = passwordEncoder.matches(plainPassword, encodedPassword);

//            boolean isPasswordMatch = passwordEncoder.matches(plainPassword, "$2a$10$Si4n7fJhO0uzBAQVyfWNaujGLKdgAf78CWzWAJcJcQ1/iUPmpLg.S");

            System.out.println("isPasswordMatch : "+isPasswordMatch);
            // Print whether the password matches
            if (isPasswordMatch) {
                System.out.println("Password matches.");
            } else {
                System.out.println("Password does not match.");
            }
        }


}
