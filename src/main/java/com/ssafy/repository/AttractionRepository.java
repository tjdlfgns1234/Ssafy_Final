package com.ssafy.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;

import com.ssafy.dto.AttractionDTO;

@Mapper
@Primary
public interface AttractionRepository {
	Map<Integer, String> getGugunByString(int sidoCode) throws SQLException;
	Map<Integer, String> getSidoByString() throws SQLException;
	Map<Integer, String> getContentByString() throws SQLException;
	List<AttractionDTO> getAttBycode(int gugunCode, int sidoCode, int contentType, String keyword) throws SQLException;
}
