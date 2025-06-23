package com.ssafy.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ssafy.dto.MemberDTO;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AOP를 이용해 MemberDTO 비밀번호를 인코딩하여 저장하도록 처리
 */
@Aspect
@Component
public class MemberAspect {
    private static final Logger log = LoggerFactory.getLogger(MemberAspect.class);
    private final PasswordEncoder pe;

    public MemberAspect(PasswordEncoder pe) {
        this.pe = Objects.requireNonNull(pe, "PasswordEncoder cannot be null");
    }

    /**
     * repository의 MemberUpdate 호출 전 MemberDTO의 비밀번호를 인코딩
     */
    @Before("execution(* com.ssafy.repository.MemberRepository.MemberUpdate(..)) && args(memberDTO)")
    public void encodePasswordBeforeUpdate(MemberDTO memberDTO) {
        if (memberDTO != null && memberDTO.getPw() != null && !memberDTO.getPw().isEmpty()) {
            String rawPw = memberDTO.getPw();
            String encodedPw = pe.encode(rawPw);
            memberDTO.setPw(encodedPw);
            log.debug("Encoded password on update for id={}", memberDTO.getId());
        }
    }
}
