package com.ssafy.security;

import java.sql.SQLException;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.dto.MemberDTO;
import com.ssafy.repository.MemberRepository;

/**
 * Security에 의해 loadUserByUsername 메서드가 호출되며
 * UserDetails를 정의하여 반환한다.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository mDao;
    private final PasswordEncoder pe;

    /**
     * 필수 필드를 주입하는 생성자
     */
    public CustomUserDetailsService(MemberRepository mDao, PasswordEncoder pe) {
        this.mDao = Objects.requireNonNull(mDao, "MemberDaoImpl cannot be null");
        this.pe = Objects.requireNonNull(pe, "PasswordEncoder cannot be null");
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberDTO user = null;
        try {
            user = mDao.MemberSelect(id);
        } catch (SQLException e) {
            throw new UsernameNotFoundException("DB error while retrieving user: " + id, e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + id);
        }
        return new CustomUserDetails(user, pe);
    }
}
