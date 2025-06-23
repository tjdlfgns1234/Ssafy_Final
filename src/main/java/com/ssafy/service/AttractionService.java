package com.ssafy.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.dto.AttractionDTO;
import com.ssafy.dto.MemberDTO;

public interface AttractionService {
	Map<Integer, String> getGugunByString(int id)  throws Exception;
	Map<Integer, String> getSidoByString() throws SQLException;
	Map<Integer, String> getContentByString() throws SQLException;
	List<AttractionDTO> getcities(int num1,int num2,int num3,String keyword) throws SQLException;
}
