package com.ssafy.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.dto.MemberDTO;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecuredConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public SecuredConfig(JwtUtil jwtUtil,
                         CustomUserDetailsService userDetailsService,
                         ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("admin").implies("user")
                .role("user").implies("guest")
                .build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 글로벌 CORS 설정: Vite dev 서버(5173)를 허용하고
     * credentials 전송은 어차피 JWT이므로 false로 해도 무방합니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 허용할 출처
        cfg.setAllowedOrigins(List.of("http://localhost:5173", "http://192.168.205.86:5173", "http://192.168.205.86:3306", "http://192.168.205.81:8080", "http://192.168.205.81:5173", "http://192.168.205.81:3306"));
        // 허용할 HTTP 메서드
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 요청 헤더
        cfg.setAllowedHeaders(List.of("*"));
        // 로그인 응답의 Authorization 헤더를 브라우저 JS에서 꺼내도록
        cfg.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        // 쿠키 기반 인증도 쓸 경우 true (JWT만 쓰면 false여도 무방)
        cfg.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
            .getSharedObject(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtLoginFilter jwtLoginFilter,
                                                   JwtVerifyFilter jwtVerifyFilter) throws Exception {
    	// TODO: Remerber me 처리
    	// TODO: Redis(인증용 서버) 연동 
    	
        http
            // CORS
            .cors().and()
            // CSRF 완전 비활성화
            .csrf(csrf -> csrf.disable())
            // 세션 사용 안 함 → 완전 Stateless
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 엔드포인트 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST,
                                 "/api/v1/members/login",
                                 "/api/v1/members/join",
                                 "/api/v1/members/findPw"
                                 ).permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/swagger-ui.html",  // TODO TEST 용 서비스 할땐 지워야함.
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/v1/posts/top3",
                        "/api/v1/members/check-email/{email}",
                        "/api/v1/members/check-id/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/members/all"
                		,"/api/v1/metrics"
                		,"/api/v1/apis")
                    .hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/v1/members/{id}",
                		"/api/v1/posts/**",
                		"/api/v1/travelplans/**",
                		"/api/v1/userplaces/**",
                		"/api/v1/comments/**",
                		"/api/v1/trip/**",
                		"/api/v1/route/**",
                        "api/v1/optimize/**"
                		)
                    .hasAnyRole("admin","user")
                .requestMatchers(HttpMethod.POST, "/api/v1/members/{id}",
                    		"api/v1/posts/**",
                    		"api/v1/travelplans/**",
                    		"api/v1/userplaces/**",
                    		"api/v1/comments/**",
                    		"api/v1/trip/**",
                            "api/v1/optimize/**"
                    		)
                    .hasAnyRole("admin","user")
                .requestMatchers(HttpMethod.PUT, "/api/v1/members/**")
                    .hasRole("user")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/members/**")
                    .hasAnyRole("admin", "user") // 회원 탈퇴는 유저도 가능
                .anyRequest().authenticated()
            )
            // 1) 로그인용 필터 (JWT 생성)
            .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
            // 2) 모든 요청 전에 JWT 검증
            .addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
            // 예외 처리
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(
                      objectMapper.writeValueAsString(
                        Map.of("status","FAIL","error","Authentication required")
                      )
                    );
                })
                .accessDeniedHandler((req, res, denied) -> {
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(
                      objectMapper.writeValueAsString(
                        Map.of("status","FAIL","error","Access denied")
                      )
                    );
                })
            );

        return http.build();
    }
}
