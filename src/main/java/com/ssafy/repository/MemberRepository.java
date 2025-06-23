package com.ssafy.repository;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.dto.MemberDTO;


@Mapper
public interface MemberRepository {
	int countById(String id) throws Exception;
	int countByEmail(String email) throws Exception;
	int MemberInsert(MemberDTO m) throws Exception;
	int MemberUpdate(MemberDTO m) throws Exception;
	int MemberDelete(String id) throws Exception;
	List<MemberDTO> MemberSelectAll() throws Exception;
	MemberDTO MemberSelect(String id) throws Exception;
	String findPassword(String id) throws SQLException;
}
