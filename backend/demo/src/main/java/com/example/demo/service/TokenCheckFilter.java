package com.example.demo.service;

import com.example.demo.service.*;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class TokenCheckFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public TokenCheckFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> accessTokenOpt = extractCookieValue(request, "JWTaccessToken");

        if (accessTokenOpt.isPresent()) {
            try {
                jwtUtil.validateAccessToken(accessTokenOpt.get());
                filterChain.doFilter(request, response);
                return;
            } catch (ExpiredJwtException e) {
                // proceed to refresh handling
            } catch (JwtException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst();
    }
}
