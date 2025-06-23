//package com.ssafy.repository;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Repository;
//
//import com.ssafy.dto.AttractionDTO;
//import com.ssafy.util.DBUtil;
//
//
//@Repository
////@Primary
//public class AttractionRepositoryImpl implements AttractionRepository{
//
//	// @Todo :  상품 정보 조회
//    public Map<Integer, String> getGugunByString(int sidoCode) throws SQLException {
//    	Connection connection = DBUtil.getConnection();
//		String sql = "select gugun_code, gugun_name from guguns where sido_code = ?";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setInt(1, sidoCode);
//		ResultSet rs = pstmt.executeQuery();
//		Map<Integer, String> map = new HashMap<>();
//		while(rs.next()) {
//			map.put(rs.getInt("gugun_code"), rs.getString("gugun_name"));
//		}
//		DBUtil.close(connection);
//		return map;
//    }
//    
//	// @Todo :  
//    public List<String> getAttBycode(int sidoCode) throws SQLException {
//    	Connection connection = DBUtil.getConnection();
//		String sql = "select title, addr1, overview=where  ?";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setInt(1, sidoCode);
//		ResultSet rs = pstmt.executeQuery();
//		List<String> list=new ArrayList<>();
//		while(rs.next()) {
//			list.add(rs.getString("gugun_code"));
//		}
//
//		DBUtil.close(connection);
//		return list;
//    }
//
//	@Override
//	public Map<Integer, String> getSidoByString() throws SQLException {
//		Connection connection = DBUtil.getConnection();
//		String sql = "select sido_code, sido_name from sidos";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		ResultSet rs = pstmt.executeQuery();
//		Map<Integer, String> map= new HashMap<>();
//		while(rs.next()) {
//			map.put(rs.getInt("sido_code"), rs.getString("sido_name"));
//		}
//
//		DBUtil.close(connection);
//		return map;
//	}
//
//	@Override
//	public Map<Integer, String> getContentByString() throws SQLException {
//		// TODO Auto-generated method stub
//		Connection connection = DBUtil.getConnection();
//		String sql = "select content_type_id, content_type_name from contenttypes";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		ResultSet rs = pstmt.executeQuery();
//		Map<Integer, String> map= new HashMap<>();
//		while(rs.next()) {
//			map.put(rs.getInt("content_type_id"), rs.getString("content_type_name"));
//		}
//
//		DBUtil.close(connection);
//		return map;
//	}
//	
//	@Override
//	public List<AttractionDTO> getAttBycode(int gugunCode, int sidoCode, int contentType) throws SQLException {
//	    Connection connection = DBUtil.getConnection();
//	    StringBuilder builder = new StringBuilder();
//	    builder.append("select * from attractions");
//	    if (gugunCode == -1 && sidoCode == -1 && contentType == -1) {
//	    	
//	    }
//	    else 
//	    	{
//	    	builder.append(" where ");
//	    	int flag = 1;
//	    	if (gugunCode != -1) {
//	    		flag = 0;
//	    		builder.append("area_code = ");
//	    		builder.append(gugunCode);
//	    		builder.append(" ");
//	    	}
//	    	if (sidoCode != -1) {
//	    		if (flag == 0) builder.append(" and ");
//	    		flag = 0;
//	    		builder.append("si_gun_gu_code = ");
//	    		builder.append(sidoCode);
//	    		builder.append(" ");
//	    	}
//	    	if (contentType != -1) {
//	    		if (flag == 0) builder.append(" and ");
//	    		flag = 0;
//	    		builder.append("content_type_id = ");
//	    		builder.append(contentType);
//	    		builder.append(" ");
//	    	}
//	    	
//	    }
//	    //String sql = "select * from attractions  where area_code = ? and si_gun_gu_code= ? and content_type_id = ?";
//	    String sql = builder.toString();
//	    System.out.println("query : " + sql);
//	    PreparedStatement pstmt = connection.prepareStatement(sql);
//	    
//	    ResultSet rs = pstmt.executeQuery();
//	    List<AttractionDTO> list=new ArrayList<>();
//	    while(rs.next()) {
//	        list.add(new AttractionDTO(rs.getInt("no"),rs.getInt("content_id"),rs.getString("title"),
//	                rs.getInt("content_type_id"),rs.getInt("area_code"),rs.getInt("si_gun_gu_code"),
//	                rs.getString("first_image1"), rs.getString("first_image2"), rs.getInt("map_level"),
//	                rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getString("addr1"), rs.getString("homepage"), rs.getString("overview")));
//	    }
//	    DBUtil.close(pstmt);
//	    DBUtil.close(connection);
//	    return list;
//	}
//
//
//    
//}
