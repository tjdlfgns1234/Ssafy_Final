//package com.ssafy.repository;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.stereotype.Repository;
//
//import com.ssafy.dto.MemberDTO;
//
//import com.ssafy.util.DBUtil;
//
//@Repository
//public class MemberRepositoryImpl implements MemberRepository {
//
//	@Override
//	public int MemberInsert(MemberDTO m) throws Exception {
//		Connection connection = DBUtil.getConnection();
//		String sql = "insert into member(id, pw, name, email, role) values(?,?,?,?,'user')";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setString(1, m.getId());
//		pstmt.setString(2, m.getPw());
//		pstmt.setString(3, m.getName());
//		pstmt.setString(4, m.getEmail());
//		
//		
//		int res = pstmt.executeUpdate();
//		DBUtil.close(pstmt);
//		DBUtil.close(connection);
//		return res;
//	}
//	
//	@Override
//	public int MemberUpdate(MemberDTO m) throws Exception {
//		Connection connection = DBUtil.getConnection();
//		String sql = "update member set  pw = ? , name = ?, email = ? where  id = ? ";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		
//		pstmt.setString(1, m.getPw());
//		pstmt.setString(2, m.getName());
//		pstmt.setString(3, m.getEmail());
//		pstmt.setString(4, m.getId());
//		
//		int res = pstmt.executeUpdate();
//		DBUtil.close(pstmt);
//		DBUtil.close(connection);
//		return res;
//	}
//
//	@Override
//	public int MemberDelete(String id) throws Exception {
//		// TODO Auto-generated method stub
//		Connection connection = DBUtil.getConnection();
//		String sql = "delete from member where id = ? ";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setString(1, id);
//		
//		int res = pstmt.executeUpdate();
//		DBUtil.close(pstmt);
//		DBUtil.close(connection);
//		return res;
//	}
//
//	@Override
//	public List<MemberDTO> MemberSelectAll() throws Exception {
//		Connection connection = DBUtil.getConnection();
//		String sql = "select id, pw, name, email from member ";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		ResultSet rs = pstmt.executeQuery();
//		List<MemberDTO> list = new ArrayList<>();
//		while(rs.next()) {
//			MemberDTO dto = new MemberDTO(rs.getString("id"),rs.getString("pw"),rs.getString("name"),rs.getString("email") );
//			list.add(dto);
//		}
//		DBUtil.close(pstmt);
//		DBUtil.close(connection);
//		return list;
//	}
//
//	@Override
//	public MemberDTO MemberSelect(String id) throws Exception {
//		// TODO Auto-generated method stub
//		Connection connection = DBUtil.getConnection();
//		String sql = "select * from member where id = ?";
//		MemberDTO member = null;
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setString(1, id);
//		ResultSet rset = pstmt.executeQuery();
//		if(rset.next()) {
//			member = new MemberDTO(rset.getString("id"),rset.getString("pw"),rset.getString("name"),rset.getString("email"));
//
//		}
//		DBUtil.close(pstmt);
//		DBUtil.close(connection);
//		return member;
//	}
//
//	@Override
//	public String findPassword(String id) throws SQLException {
//		// TODO Auto-generated method stub
//		Connection connection = DBUtil.getConnection();
//		String sql = "select pw from member where id = ?";
//		PreparedStatement pstmt = connection.prepareStatement(sql);
//		pstmt.setString(1, id);
//		ResultSet rs = pstmt.executeQuery();
//		String pw = null;
//		if(rs.next()) {
//			pw = rs.getString("pw");
//		}
//		
//		DBUtil.close(connection);
//		return pw;
//	}
//
//
//
//}
