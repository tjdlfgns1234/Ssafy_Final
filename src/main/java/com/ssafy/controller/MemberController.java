package com.ssafy.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssafy.dto.MemberDTO;
import com.ssafy.service.MemberService;
import com.ssafy.service.MemberServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/member")
// @Controller
//@RequestMapping({"/", "/member"})
public class MemberController{
	
	@Autowired
	private MemberService service;
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		process(req,resp);
//	}
//	
//	
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		process(req,resp);
//	}
//
//
//	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
//		String action = req.getParameter("action");
//		String url = new String();
//		try {
//			if (action == null && action.isBlank()) {
//				url = "index.jsp";
//			} else {
//				if(action.equals("/")) {
//					
//				} else if (action.equals("memberinsert")) {
//					url = memberInsert(req, resp);
//				} else if (action.equals("memberdelete")) {
//					url = memberDelete(req, resp);
//				} else if (action.equals("memberupdate")) {
//					url = memberUpdate(req, resp);
//				}  else if (action.equals("login")) {
//					url = memberLogin(req, resp);
//				}  else if (action.equals("logout")) {
//					url = memberLogout(req, resp);
//				} else if (action.equals("mypage")) {
//					url = mypage(req, resp);
//				} else if (action.equals("findpw")) {
//					findPw(req, resp);
//					return ;
//				} 
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		if(url.startsWith("redirect")) {
//			url = url.substring(url.indexOf(":")+1);
//			resp.sendRedirect(req.getContextPath()+url);
//		}else {
//			req.getRequestDispatcher(url).forward(req, resp);
//		}
//	}

//	@GetMapping({"/", "/index"})
//	public String index() {
//		return "index";
//	}
//	
//	@GetMapping({"/error/403"})
//	public String error403() {
//		return "error/403";
//	}
//	
//	@GetMapping("gotoregisterpage")
//	public String gotoregisterpage() {
//		return "member/register";
//	}
//	
//	@GetMapping("gotologinpage")
//	public String gotologinpage() {
//		return "member/login";
//	}
//	
//	
//	@PostMapping("findPw")
//	private void findPw(@RequestParam MemberDTO user
//	, HttpServletResponse resp) throws Exception {		
////		String Id = req.getParameter("findid");
////		String Name = req.getParameter("findname");
////		String Email = req.getParameter("findemail");
//		String pw = service.findPassword(user.getId());
//		
//		 resp.setContentType("text/plain");
//		 resp.setCharacterEncoding("UTF-8");
//		 System.out.println(pw);
//	    if (pw != null) {
//	        resp.getWriter().write(pw);
//	    } else {
//	        resp.getWriter().write("");
//	    }
//	    
//	    return ;
//	}
//
//	@PostMapping("memberupdate")
//	private String memberUpdate(@ModelAttribute MemberDTO user
//			, HttpSession session 
//			, Model model) throws Exception {
////		String Id = req.getParameter("userid");
////		String Pw = req.getParameter("password");
////		String Name = req.getParameter("name");
////		String Email = req.getParameter("email");
////		MemberDTO m = new MemberDTO(Id, Pw, Name,Email);
//		// System.out.println(user);
//		service.MemberUpdate(user);
//		MemberDTO m = service.MemberSelect(user.getId());
//		session.setAttribute("loginUser", m);
//		return mypage(model, user.getId());
//	}
//
//
//	@PostMapping("memberdelete")
//	private String memberDelete(@RequestParam("id") String Id
//			, HttpSession session) throws Exception {
//		int res = service.MemberDelete(Id);
//		return memberLogout(session);
//	}
//
//	@RequestMapping(value = "/mypage", method = {RequestMethod.GET, RequestMethod.POST})
//	private String mypage(Model model
//			, @RequestParam("id") String Id) throws Exception {
//		MemberDTO member = service.MemberSelect(Id);
//		model.addAttribute("member", member);
//		return "member/mypage";
//	}

	// @GetMapping("logout")
	private String memberLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/index";
	}

   //  @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        model.addAttribute("loginError", error != null);
        return "member/login";  // member/login.jsp
    }
	
	
	// @PostMapping("login")
	private String memberLogin(@RequestParam(value = "remember-me", required = false) String rememberMe
			, @RequestParam("userid") String Id
			, @RequestParam("password") String Pw
			, HttpSession session
			, HttpServletResponse resp) throws Exception {
//		String Id = req.getParameter("userid");
//		String Pw = req.getParameter("password");
//		String rememberMe = req.getParameter("remember-me");
		try {
			MemberDTO member = service.login(Id,Pw);
			session.setAttribute("loginUser", member);
			if (rememberMe == null) {
				Cookie cookie = new Cookie("rememberMe", "bye");
		    	cookie.setMaxAge(0);
		    	cookie.setPath(null);
		    	resp.addCookie(cookie);
	        } else {
	        	Cookie cookie = new Cookie("rememberMe", Id);
		    	cookie.setMaxAge(60 * 60 * 24 * 365);
		    	cookie.setPath(null);
		    	resp.addCookie(cookie);
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/index";
	}

	@PostMapping("memberinsert")
	private String memberInsert(@ModelAttribute MemberDTO member
			, HttpServletResponse resp) throws Exception {
//		String Id = req.getParameter("userid");
//		String Pw = req.getParameter("password");
//		String Name = req.getParameter("name");
//		String Email = req.getParameter("email");
//		MemberDTO m = new MemberDTO(Id, Pw, Name,Email);
		service.MemberInsert(member);
		return "/member/login";
	}
}
