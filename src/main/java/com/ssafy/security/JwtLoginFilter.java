package com.ssafy.security;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.dto.MemberDTO;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtLoginFilter(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          ObjectMapper objectMapper) {
        super.setAuthenticationManager(authManager);
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        // 이 필터가 처리할 로그인 URL
        setFilterProcessesUrl("/api/v1/members/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) {
        try {
            // JSON { "id": "...", "pw": "..." } 바디 파싱
            var creds = new ObjectMapper()
                .readValue(req.getInputStream(), MemberDTO.class);
            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                  creds.getId(), creds.getPw()
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            jakarta.servlet.FilterChain chain,
                                            Authentication auth) 
                                            throws IOException, ServletException {
        String username = auth.getName();
        var roles = auth.getAuthorities()
                        .stream()
                        .map(a->a.getAuthority())
                        .collect(Collectors.toList());
        String token = jwtUtil.generateToken(username, roles);

        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(res.getWriter(),
            Map.of("status","SUCCESS","token",token)
        );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req,
                                              HttpServletResponse res,
                                              org.springframework.security.core.AuthenticationException ex)
                                              throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(res.getWriter(),
            Map.of("status","FAIL","error",ex.getMessage())
        );
    }
}
