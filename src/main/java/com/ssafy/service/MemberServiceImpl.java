package com.ssafy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.dto.MemberDTO;
import com.ssafy.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repo;

    // 생성자 주입 방식
    public MemberServiceImpl(MemberRepository repo) {
        this.repo = repo;
    }

    @Override
    public int MemberInsert(MemberDTO m) throws Exception {
        return repo.MemberInsert(m);
    }

    @Override
    public int MemberUpdate(MemberDTO m) throws Exception {
        return repo.MemberUpdate(m);
    }

    @Override
    public int MemberDelete(String id) throws Exception {
        return repo.MemberDelete(id);
    }

    @Override
    public List<MemberDTO> MemberSelectAll() throws Exception {
        return repo.MemberSelectAll();
    }

    @Override
    public MemberDTO MemberSelect(String id) throws Exception {
        return repo.MemberSelect(id);
    }

    @Override
    public String findPassword(String id) throws Exception {
        return repo.findPassword(id);
    }

    @Override
    public MemberDTO login(String id, String pw) throws Exception {
        MemberDTO member = repo.MemberSelect(id);
        if (member != null && member.getPw().equals(pw)) {
            return member;
        } else {
            return null;
        }
    }

    @Override
    public int countById(String id) throws Exception {
        return repo.countById(id);
    }

    @Override
    public int countByEmail(String email) throws Exception {
        return repo.countByEmail(email);
    }
}
