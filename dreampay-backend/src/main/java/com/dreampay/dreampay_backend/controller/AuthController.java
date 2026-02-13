package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.entity.User;
import com.dreampay.dreampay_backend.repository.UserRepository;
import com.dreampay.dreampay_backend.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtil jwtUtil;

        // REGISTER
        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody User user) {

            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());

            return ResponseEntity.ok(userRepository.save(user));
        }

        // LOGIN
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody User request) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid password");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());



            return ResponseEntity.ok(token);
        }
    }



