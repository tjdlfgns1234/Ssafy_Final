<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="/assets/css/register.css" />
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
	<div class="register_container">
      <h2 class="text-center mb-4">회원가입</h2>
      <form class="needs-validation" action="${root }/member/memberinsert" method="POST" novalidate>
          <!-- 이름 입력 -->
          <div class="mb-3">
              <label for="name" class="form-label">이름</label>
              <input type="text" class="form-control" name="name" placeholder="이름 입력" required>
              <div class="invalid-feedback">이름을 입력하세요.</div>
          </div>
  
          <!-- 아이디 입력 -->
          <div class="mb-3">
              <label for="userid" class="form-label">아이디</label>
              <input type="text" class="form-control" name="id" placeholder="아이디 입력" required>
              <div class="invalid-feedback">아이디를 입력하세요.</div>
          </div>
  
          <!-- 비밀번호 입력 -->
          <div class="mb-3">
              <label for="password" class="form-label">비밀번호</label>
              <input type="password" class="form-control" name="pw" placeholder="비밀번호 입력" required>
              <div class="invalid-feedback">비밀번호를 입력하세요.</div>
          </div>
  
          <!-- 비밀번호 확인 -->
          <div class="mb-3">
              <label for="password_check" class="form-label">비밀번호 확인</label>
              <input type="password" class="form-control" name="password_check" placeholder="비밀번호 확인" required>
              <div class="invalid-feedback">비밀번호 확인을 입력하세요.</div>
          </div>
  
          <!-- 이메일 입력 -->
          <div class="mb-3">
              <label for="email" class="form-label">이메일</label>
              <div class="input-group" >
                  <input type="text" class="form-control" id="email" name="email" placeholder="이메일 입력" required oninput="updateEmail()" >
        		<span class="input-group-text">@</span>
		        <select class="form-select" id="domainSelect" required onchange="updateEmail()">
		            <option value="">도메인 선택</option>
		            <option value="ssafy.com">SSAFY</option>
		            <option value="google.com">GOOGLE</option>
		            <option value="kakao.com">KAKAO</option>
		            <option value="naver.com">NAVER</option>
		        </select>
          
              </div>
          </div>
  
          <!-- 버튼 -->
          <div class="d-flex justify-content-end">
              <button type="submit" class="btn btn-primary me-2">회원가입</button>
              <button type="button" class="btn btn-danger" onclick="window.location.href='${root }/member'">취소</button>
          </div>
      </form>
  </div>

    <script src="${root }/assets/js/checkout.js"></script>
    
	<script>
		function updateEmail() {
		    var emailInput = document.getElementById("email");
		    var domainSelect = document.getElementById("domainSelect");
		    var emailValue = emailInput.value.split("@")[0]; // '@' 앞부분만 가져오기
		
		    if (domainSelect.value) {
		        emailInput.value = emailValue + "@" + domainSelect.value;
		    } else {
		        emailInput.value = emailValue; // 도메인이 없으면 '@' 제외
		    }
		}
	</script>
</body>
</html>