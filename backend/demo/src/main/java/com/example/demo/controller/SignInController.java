package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtUtil;
import com.example.demo.service.PasswordUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@CrossOrigin(origins = "*")
public class SignInController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public SignInController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody User loginRequest, HttpServletResponse response) {
        Optional<User> dbUserOpt = userRepository.findById(loginRequest.getId());

        if (dbUserOpt.isEmpty() ||
            !PasswordUtils.checkPassword(loginRequest.getPassword(), dbUserOpt.get().getPassword())) {
            // ID の存在有無を開示しないように、同じメッセージを返す
            return ResponseEntity.status(401).body("Invalid ID or password");
        }

        User dbUser = dbUserOpt.get();

        // Create Access Token
        String accessToken = jwtUtil.createAccessToken(dbUser.getId(), dbUser.getRoleLevel());
        Cookie accessCookie = jwtUtil.createAccessCookie(accessToken);

        response.addCookie(accessCookie);
        
        // TODO: リフレッシュトークンの発行・DB保存・クッキー送信処理を追加する

        
        return ResponseEntity.ok("You were logged in!");
    }
}
