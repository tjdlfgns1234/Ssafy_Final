package com.ssafy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ssafy.dto.MemberDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * UserDetailService에 의해 생성되며 user에서 정의했으면 하는 기능을 구현한다
 */
public class CustomUserDetails implements UserDetails {
    private MemberDTO user;
    private PasswordEncoder pe;

    /**
     * 필수 필드만 초기화하는 생성자
     */
    @Autowired
    public CustomUserDetails(MemberDTO user, PasswordEncoder pe) {
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.pe = Objects.requireNonNull(pe, "passwordEncoder cannot be null");
    }

    // user getter / setter
    public MemberDTO getUser() {
        return user;
    }

    public void setUser(MemberDTO user) {
        this.user = user;
    }

    // passwordEncoder getter / setter
    public PasswordEncoder getPasswordEncoder() {
        return pe;
    }

    public void setPasswordEncoder(PasswordEncoder pe) {
        this.pe = pe;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roles = new ArrayList<>();
        String role = user.getRole();
        if (role != null && !role.isEmpty()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return roles;
    }

    @Override
    public String getPassword() {
        // 비밀번호는 인코딩된 형태로 반환
        return user.getPw();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
               "user=" + user +
               ", passwordEncoder=" + pe +
               "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(pe, that.pe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, pe);
    }
}
