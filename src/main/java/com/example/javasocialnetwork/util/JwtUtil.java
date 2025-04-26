package com.example.javasocialnetwork.util;


import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtil {

    // Генерация безопасного ключа для HS256
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // токен на 1 день
                .signWith(SECRET_KEY)  // Используем безопасный ключ
                .compact();
    }

    public String extractUsername(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()  // Используем новый метод parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build();
        return jwtParser.parseClaimsJws(token)  // И вызываем parseClaimsJws
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        return extractExpiration(token).after(new Date()); // проверка, не истек ли токен
    }

    private Date extractExpiration(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()  // Используем новый метод parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build();
        return jwtParser.parseClaimsJws(token)  // И вызываем parseClaimsJws
                .getBody()
                .getExpiration();
    }
}

