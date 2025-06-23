package com.ssafy.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.dto.AttractionDTO;
import com.ssafy.repository.AttractionRepository;

@Service
public class AttractionServiceImpl implements AttractionService{
	@Autowired
	AttractionRepository repo;
//	public AttractionServiceImpl()  {
//		repo = new AttractionRepository();
//	}
	@Override
	public Map<Integer, String> getGugunByString(int id) throws Exception {
		// TODO Auto-generated method stub
		return repo.getGugunByString(id);
	}
	@Override
	public Map<Integer, String> getSidoByString() throws SQLException {
		return repo.getSidoByString();
	}
	
	@Override
	public Map<Integer, String> getContentByString() throws SQLException {
		// TODO Auto-generated method stub
		return repo.getContentByString();
	}
	
	@Override
	public List<AttractionDTO> getcities(int num1,int num2,int num3, String keyword) throws SQLException{
		
	    return repo.getAttBycode(num1, num2, num3, keyword);
	}
}
