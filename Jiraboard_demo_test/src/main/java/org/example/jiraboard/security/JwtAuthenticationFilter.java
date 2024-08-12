package org.example.jiraboard.security;

import org.example.jiraboard.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Entering JwtAuthenticationFilter.doFilterInternal()");

        String jwt = parseJwt(request);
        logger.debug("Parsed JWT from request: {}", jwt);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            logger.debug("JWT is valid. Extracting username.");

            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            logger.debug("Username extracted from JWT: {}", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("Loaded UserDetails for username: {}", username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            logger.debug("Created UsernamePasswordAuthenticationToken for user: {}", username);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            logger.debug("Set authentication details for the current request.");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Set SecurityContextHolder authentication for user: {}", username);
        } else {
            logger.debug("JWT is either null or invalid.");
        }

        filterChain.doFilter(request, response);
        logger.debug("Completed filtering request in JwtAuthenticationFilter.");
    }

    private String parseJwt(HttpServletRequest request) {
        logger.debug("Parsing JWT from Authorization header.");

        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            logger.debug("Authorization header found with Bearer token.");
            return headerAuth.substring(7);
        }

        logger.debug("No Bearer token found in Authorization header.");
        return null;
    }
}
