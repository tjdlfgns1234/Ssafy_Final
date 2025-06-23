//package com.ssafy.util;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Scanner;
//
//import com.ssafy.dto.MemberDTO;
//import com.ssafy.service.MemberService;
//import com.ssafy.service.MemberServiceImpl;
//
//public class MemberTest{
//	private static MemberService service;
//	
//	public static void main(String[] args) throws Exception {
//		
//		System.out.println("원하시는 기능을 선택하세요.");
//		System.out.println("1.회원가입 2.회원수정 3.회원삭제 4.회원조회 5.비밀번호찾기");
//		Scanner sc = new Scanner(System.in);
//		
//		
//		int m_num = sc.nextInt();
//		switch (m_num) {
//		case 1 -> MemberInsert();
//		case 2 -> MemberUpdate();
//		case 3 -> MemberDelete();
//		case 4 -> MemberSelectAll();
//		case 5 -> FindPassword();
//		
//		default ->
//		throw new IllegalArgumentException("Unexpected value: " + m_num);
//		}
//		
//		
//	}
//
//	private static void FindPassword() throws Exception {
//		System.out.println("비밀번호를 찾고 싶은 아이디를 입력하세요. ");
//		Scanner sc = new Scanner(System.in);
//		String id = sc.next();
//		String pw = service.findPassword(id);
//		System.out.println(pw);
//	}
//
//	private static void MemberUpdate() throws Exception {
//		// TODO Auto-generated method stub
//		List<MemberDTO> list = service.MemberSelectAll();
//		for (MemberDTO member : list) {
//			System.out.print("ID: " + member.getId() + ",");
//			
//			System.out.print("PW: " + member.getPw()+ ",");
//			System.out.print("Name: " + member.getName()+ ",");
//			System.out.println("Email: " + member.getEmail()+ ",");
//		}
//		System.out.println("수정하고 싶은 ID를 입력하세요: ");
//		Scanner sc = new Scanner(System.in);
//		String id = sc.next();
//		System.out.println("수정할 데이터를 입력하세요: ");
//		System.out.println("비밀번호: ");
//		String pw = sc.next();
//		System.out.println("이름: ");
//		String name = sc.next();
//		System.out.println("이메일: ");
//		String email = sc.next();
//		service.MemberUpdate(new MemberDTO(id,pw,name,email));
//		System.out.println("수정하였습니다.");
//		
//	}
//
//	private static void MemberDelete() throws Exception {
//		// TODO Auto-generated method stub
//		List<MemberDTO> list = service.MemberSelectAll();
//		for (MemberDTO member : list) {
//			System.out.print("ID: " + member.getId() + ",");
//			
//			System.out.print("PW: " + member.getPw()+ ",");
//			System.out.print("Name: " + member.getName()+ ",");
//			System.out.println("Email: " + member.getEmail()+ ",");
//		}
//		System.out.println("삭제하고 싶은 ID를 입력하세요: ");
//		Scanner sc = new Scanner(System.in);
//		String id = sc.next();
//		service.MemberDelete(id);
//		System.out.println("삭제되었습니다.");
//	}
//
//	private static void MemberSelectAll() throws Exception {
//		// TODO Auto-generated method stub
//		List<MemberDTO> list = service.MemberSelectAll();
//		for (MemberDTO member : list) {
//			System.out.print("ID: " + member.getId() + ",");
//			
//			System.out.print("PW: " + member.getPw()+ ",");
//			System.out.print("Name: " + member.getName()+ ",");
//			System.out.println("Email: " + member.getEmail()+ ",");
//		}
//	
//	}
//
//	private static void MemberInsert() throws Exception {
//		Scanner sc = new Scanner(System.in);
//		System.out.println("아이디: ");
//		String id = sc.next();
//		System.out.println("비밀번호: ");
//		String pw = sc.next();
//		System.out.println("이름: ");
//		String name = sc.next();
//		System.out.println("이메일: ");
//		String email = sc.next();
//		service.MemberInsert(new MemberDTO(id,pw,name,email));
//	}
//}
