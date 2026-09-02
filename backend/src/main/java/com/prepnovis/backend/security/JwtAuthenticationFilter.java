package com.prepnovis.backend.security;

import java.io.IOException;

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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    
   @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    System.out.println("\n=== JWT FILTER START ===");
    System.out.println("Request URI: " + request.getRequestURI());

    String authHeader = request.getHeader("Authorization");

    System.out.println("Authorization header: " + authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        System.out.println("No Bearer token found");
        System.out.println("=== JWT FILTER END ===\n");

        filterChain.doFilter(request, response);
        return;
    }

    String jwt = authHeader.substring(7);

    System.out.println("JWT found");

    String email;

    try {

        email = jwtService.extractUsername(jwt);

        System.out.println("Email extracted from JWT: " + email);

    } catch (Exception ex) {

        System.out.println("JWT parsing FAILED");
        System.out.println("Exception: " + ex.getMessage());

        ex.printStackTrace();

        filterChain.doFilter(request, response);
        return;
    }

    if (email != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {

        System.out.println("Loading user from database...");

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

        System.out.println("User loaded: " + userDetails.getUsername());

        boolean valid = jwtService.isTokenValid(jwt, userDetails);

        System.out.println("JWT valid: " + valid);

        if (valid) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            var context = SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            System.out.println(
                    "Authentication object: "
                            + SecurityContextHolder.getContext()
                                    .getAuthentication()
            );

            System.out.println(
                    "isAuthenticated: "
                            + SecurityContextHolder.getContext()
                                    .getAuthentication()
                                    .isAuthenticated()
            );
        }
    }

    System.out.println("Continuing filter chain...");
    System.out.println("=== JWT FILTER END ===\n");

    filterChain.doFilter(request, response);
}
}