package com.ssafy.security;

import java.security.Key;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMs = 3_600_000; // 1시간

    /**
     * username(subject)과 roles(claim) 을 설정하고 토큰을 생성합니다.
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + validityInMs))
            .signWith(key)
            .compact();
    }

    /**
     * 토큰이 유효한지 파싱을 시도하여 검증합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 subject(username) 를 꺼내 반환합니다.
     */
    public String getUsername(String token) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
