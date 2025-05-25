package com.example.demo.Models;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key key = Keys.hmacShaKeyFor("tajny_klucz123456789012345678901234567890".getBytes()); // min 32 znaki

    public static String generateToken(String email, String role,Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 godzina
                .signWith(key)
                .compact();

    }
}

