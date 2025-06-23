<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="/assets/css/mypage.css" />
    <script src="/assets/js/mypage.js"></script>

</head>
<body>
	<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
	<div class="register_container">
        <h2 class="text-center mb-4">마이페이지</h2>
        <form class="needs-validation" action="" method="post"  id="rform" novalidate >
            <!-- 이름 입력 -->
            <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" class="form-control" id="name" name="name" value="${loginUser.name }" placeholder="이름 입력" required>
                <div class="invalid-feedback">이름을 입력하세요.</div>
            </div>
    
            <!-- 아이디 입력 -->
            <div class="mb-3">
                <label for="userid" class="form-label">아이디</label>
                <input type="text" class="form-control" id="userid" name="id" value="${loginUser.id }" placeholder="아이디 입력" required readonly> 
                <div class="invalid-feedback">아이디를 입력하세요.</div>
            </div>
    
            <!-- 비밀번호 입력 -->
            <div class="mb-3">
                <label for="password" class="form-label">비밀번호</label>
                <input type="password" class="form-control" id="password" name="pw" value="${loginUser.pw }"placeholder="비밀번호 입력" required>
                <div class="invalid-feedback">비밀번호를 입력하세요.</div>
            </div>
    
            <!-- 비밀번호 확인 -->
          <div class="mb-3">
            <label for="password_check" class="form-label">비밀번호 확인</label>
            <input type="password" class="form-control" id="password_check" placeholder="비밀번호 확인" required>
            <div class="invalid-feedback">비밀번호 확인을 입력하세요.</div>
        </div>
            <!-- 이메일 입력 -->
            <div class="mb-3">
                <label for="email" class="form-label">이메일</label>
                <div class="input-group">
                    <input type="text" class="form-control" id="email" name="email" value="${loginUser.email }" placeholder="이메일 입력" required>
                    <span class="input-group-text">@</span>
                    <select class="form-select" id="domainSelect" required onchange="updateEmail()">
                        <option value="">도메인 선택</option>
                        <option value="ssafy.com">SSAFY</option>
                        <option value="google.com">GOOGLE</option>
                        <option value="kakao.com">KAKAO</option>
                        <option value="naver.com">NAVER</option>
                    </select>
                    <div class="invalid-feedback">이메일을 올바르게 입력하세요.</div>
                </div>  
            </div>
    
            <!-- 버튼 -->
            <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-primary me-2"  >수정</button>
                <button type="button" id="quit" class="btn btn-danger  me-2" onclick="deletemember()">탈퇴</button>
            </div>

            <div class="d-flex justify-content-end">
              <button type="submit" id="confirm" class="btn btn-primary me-2" onclick="updatemember()">확인</button>
              <button type="button" id="cancel" class="btn btn-danger  me-2" onclick="window.location.href='${root }/'">취소</button>
          </div>
        </form>
    </div>
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
		function updatemember(){
			// console.log("sdssd");
			//alert("update");
			document.getElementById("rform").action = "${root}/member/memberupdate";
			document.getElementById("rform").submit();
		}
		function deletemember(){
			//alert("delete");
			document.getElementById("rform").action = "${root}/member/memberdelete?id=${loginUser.id}";
			document.getElementById("rform").submit();
		}
	</script>
</body>
</html>