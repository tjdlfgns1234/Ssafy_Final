package com.ssafy.controller;

import com.ssafy.dto.MemberDTO;
import com.ssafy.service.MemberService;

import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;
import java.util.Map;

/**
 * DAO 기반 Service를 사용하는 REST 방식의 Member API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/members")
public class MemberRestController {

    private final MemberService service;
    private final PasswordEncoder passwordEncoder;

    public MemberRestController(MemberService service,
                                PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    /** 전체 회원 조회 (GET /api/v1/members) */
    @GetMapping("/all")
    public ResponseEntity<?> listAll() {
        try {
            List<MemberDTO> all = service.MemberSelectAll();
            return ResponseEntity.ok(all);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 단일 회원 조회 (GET /api/v1/members/{id}) */
    @Timed(
    	      value       = "members.getOne",
    	      description = "단일 회원 조회에 소요된 시간",
    	      histogram   = true,
    	      percentiles = {0.5, 0.95}
    	    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        try {
            MemberDTO m = service.MemberSelect(id);
            if (m == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "FAIL", "error", "Member not found"));
            }
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "member", m));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 회원 가입 (POST /api/v1/members) */
    @PostMapping("/join")
    public ResponseEntity<?> create(@RequestBody MemberDTO payload) {
        try {
            // ① 평문 → 암호문
            String rawPw = payload.getPw();
            String encPw = null;
			try {
				encPw = passwordEncoder.encode(rawPw);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            payload.setPw(encPw);

            // ② DB 저장
            service.MemberInsert(payload);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("status", "SUCCESS", "member", payload));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 회원 수정 (PUT /api/v1/members/{id}) */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody MemberDTO payload) {
        try {
            payload.setId(id);
            service.MemberUpdate(payload);
            MemberDTO updated = service.MemberSelect(id);
            return ResponseEntity
                    .ok(Map.of("status", "SUCCESS", "member", updated));
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", dae.getMessage()));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 회원 삭제 (DELETE /api/v1/members/{id}) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.MemberDelete(id);
            return ResponseEntity
                    .ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 로그인 (POST /api/v1/members/login) */
    // @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds,
                                   HttpSession session,
                                   HttpServletResponse resp) {
        String id = creds.get("id");
        String pw = creds.get("pw");
        System.out.println(id + " " + pw);
        try {
            MemberDTO member = service.login(id, pw);
            session.setAttribute("loginUser", member);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "member", member));
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "FAIL", "error", dae.getMessage()));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 비밀번호 찾기 (POST /api/v1/members/findPw) */
    @PostMapping("/findPw")
    public ResponseEntity<?> findPw(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        try {
            String pw = service.findPassword(id);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "password", pw == null ? "" : pw
            ));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }
    /** ID 중복확인 (GET /api/v1/members/check-id/{id}) */
    @GetMapping("/check-id/{id}")
    public ResponseEntity<?> checkId(@PathVariable String id) {
        try {
            int count = service.countById(id);
            boolean exists = count > 0;
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "exists", exists,
                    "count", count
            ));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** Email 중복확인 (GET /api/v1/members/check-email/{email}) */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        try {
            int count = service.countByEmail(email);
            boolean exists = count > 0;
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "exists", exists,
                    "count", count
            ));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

}
