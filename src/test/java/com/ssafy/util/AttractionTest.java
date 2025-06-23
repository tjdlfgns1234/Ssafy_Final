//package com.ssafy.util;
//
//import java.util.*;
//
//import com.ssafy.dto.AttractionDTO;
//import com.ssafy.service.AttractionServiceImpl;
//import com.ssafy.service.MemberService;
//
//public class AttractionTest {
//	private static AttractionServiceImpl service;
//	public static void main(String[] args) throws Exception {
//		service = new AttractionServiceImpl();
//		Scanner sc = new Scanner(System.in);
//		System.out.println("원하는 지역, 컨텍츠를 찾아봐");
//		Map<Integer, String> sido = service.getSidoByString();
//		System.out.println("원하는 시/도를 선택하세요.");
//		for (Integer key : sido.keySet()) {
//			System.out.print(key + " " + sido.get(key) + "/ ");
//		}
//		System.out.println();
//		int sido_id = sc.nextInt();
//		
//		Map<Integer, String> gugun = service.getGugunByString(sido_id);
//		System.out.println("원하는 구/군을 선택하세요.");
//		for (Integer key : gugun.keySet()) {
//			System.out.print(key + " " + gugun.get(key) + "/ ");
//		}
//		System.out.println();
//		int gugun_id = sc.nextInt();
//		
//		Map<Integer, String> content = service.getContentByString();
//		System.out.println("원하는 컨텐츠를 선택하세요.");
//		for (Integer key : content.keySet()) {
//			System.out.print(key + " " + content.get(key) + "/ ");
//		}
//		System.out.println();
//		int content_id = sc.nextInt();
//		
//		List<AttractionDTO> k = service.getcities(sido_id, gugun_id, content_id);
//		
////	    for (AttractionDTO att : k) {
////	        System.out.println(att.toString());
////	    }
////	    
//	    
//	}
//
//}
