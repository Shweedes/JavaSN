package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.AuthResponse;
import com.example.javasocialnetwork.dto.LoginRequestDto;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.InvalidCredentialsException;
import com.example.javasocialnetwork.service.UserService;
import com.example.javasocialnetwork.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid User user) {
        userService.registration(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDto loginRequest) {
        // Поиск пользователя по имени
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Проверка пароля
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Генерация токена на основе имени пользователя
            String token = jwtUtil.generateToken(user.getUsername()); // Передаем username
            return ResponseEntity.ok(new AuthResponse(token)); // Возвращаем токен
        } else {
            // Если пароль неверный
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}

