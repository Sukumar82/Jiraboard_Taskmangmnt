package org.example.jiraboard.security;

import org.example.jiraboard.model.User;
import org.example.jiraboard.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final UserService userService;

    @Autowired
    public CustomAuthenticationProvider(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("Starting authentication process...");
// Print (log) the entire authentication object
        logger.debug("Authentication object: {}", authentication);
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        logger.debug("Received authentication request for username: {} password: {}", username,password);

        User user = userService.loadUserEntityByUsername(username);
        if (user == null) {
            logger.error("User with username '{}' not found.", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        logger.debug("User '{}' found in the database. password:{}", username,password);

        if (userService.checkPassword(user, password)) {
            logger.debug("Password for user '{}' is valid.", username);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
            logger.info("User '{}' authenticated successfully.", username);

            return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        } else {
            logger.error("Invalid password for user '{}'.", username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.debug("Checking if the authentication class is supported: {}", authentication.getName());
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
