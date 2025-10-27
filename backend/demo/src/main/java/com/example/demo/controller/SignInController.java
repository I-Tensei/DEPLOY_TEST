package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtUtil;
import com.example.demo.service.PasswordUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@CrossOrigin(origins = "*")
public class SignInController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(SignInController.class);

    public SignInController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody User loginRequest, HttpServletResponse response) {
        String reqId = loginRequest.getId() == null ? null : loginRequest.getId().trim();
        String reqPass = loginRequest.getPassword() == null ? null : loginRequest.getPassword();

        if (reqId == null || reqId.isEmpty() || reqPass == null) {
            return ResponseEntity.status(401).body("Invalid ID or password");
        }

        Optional<User> dbUserOpt = userRepository.findById(reqId);
        if (dbUserOpt.isEmpty()) {
            log.debug("signin: user not found id={}", reqId);
            return ResponseEntity.status(401).body("Invalid ID or password");
        }

        User dbUser = dbUserOpt.get();
        boolean matches = PasswordUtils.checkPassword(reqPass, dbUser.getPassword());
        if (!matches) {
            log.debug("signin: password mismatch id={} hashPrefix={}", reqId,
                    dbUser.getPassword() != null && dbUser.getPassword().length() >= 7 ? dbUser.getPassword().substring(0,7) : "null");
            return ResponseEntity.status(401).body("Invalid ID or password");
        }

        // Create Access Token
        String accessToken = jwtUtil.createAccessToken(dbUser.getId(), dbUser.getRoleLevel());
        Cookie accessCookie = jwtUtil.createAccessCookie(accessToken);
        response.addCookie(accessCookie);

        // 成功時は軽量な情報を返す（フロントで遷移分岐に使用可）
        return ResponseEntity.ok(Map.of(
                "message", "You were logged in!",
                "roleLevel", dbUser.getRoleLevel()
        ));
    }
}
