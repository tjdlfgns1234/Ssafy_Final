package com.ssafy.service;

import java.sql.SQLException;
import java.util.List;

import com.ssafy.dto.MemberDTO;

public interface MemberService {
    int MemberInsert(MemberDTO m) throws Exception;
    int MemberUpdate(MemberDTO m) throws Exception;
    int MemberDelete(String id) throws Exception;
    List<MemberDTO> MemberSelectAll() throws Exception;
    MemberDTO MemberSelect(String id) throws Exception;
    String findPassword(String id) throws Exception;
    MemberDTO login(String id, String pw) throws Exception;
    int countById(String id) throws Exception; 
    int countByEmail(String email) throws Exception; 
}
