package com.example.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;   
import javax.crypto.SecretKey;         
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    private SecretKey accessKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
    }
    private SecretKey refreshKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String userId, int roleLevel) {
        return Jwts.builder()
                .subject(userId)
                .claim("roleLevel", roleLevel)   
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(accessKey()) 
                .compact();
 
    }

    public String createRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .expiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(refreshKey())
                .compact();
    }

    public Claims validateAccessToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(accessKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims validateRefreshToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(refreshKey()) 
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    

    public Cookie createAccessCookie(String accessToken) {
        Cookie cookie = new Cookie("accessCookie", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15); // 15min
        return cookie;
    }

    public Cookie createRefreshCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshCookie", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30day
        return cookie;
    }
}
