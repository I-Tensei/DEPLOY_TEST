package com.example.demo.controller;

import com.example.demo.service.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthMeController {

    private final JwtUtil jwtUtil;

    public AuthMeController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        // クッキーからアクセストークンを取得
        String token = extractCookie(request, "JWTaccessToken");
        if (token == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "No access token"));
        }

        // トークンを検証
        Claims claims;
        try {
            claims = jwtUtil.validateAccessToken(token);
        } catch (JwtException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        // トークンからユーザー情報を取得
        String userId = claims.getSubject(); // createAccessTokenで .subject(userId)
        Integer roleLevel = claims.get("roleLevel", Integer.class);

        if (userId == null || roleLevel == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Missing claims"));
        }

        // JSONレスポンスとして返却（userNameはuserIdと同じ）
        // TODO: include userName in token
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "userName", userId,
                "roleLevel", roleLevel
        ));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
