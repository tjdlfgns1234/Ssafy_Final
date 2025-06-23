<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
    <link rel="stylesheet" href="/assets/css/login_2.css" />
    <script src="./assets/js/nav.js"></script>
</head>
<body>
		<%@ include file="/WEB-INF/views/fragments/header.jsp"%>


	<div class="login_container">
      <h2>로그인</h2>
      <form action="${root }/member/login" method="POST">

        <div class="input">
          <label for="userid">아이디</label>
          <div>
            <input type="text" id="userid" name="userid" placeholder="아이디 입력" value="${cookie.rememberMe.value }" required="required" />
          </div>
        </div>
        <div class="input">
          <label for="password">비밀번호</label>
          <div>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="비밀번호 입력"
              required="required"
            />
          </div>
        </div>
               
         <div class="d-flex justify-content-start align-items-center">
            <div class="form-check">
                <input type="checkbox" value="on" name="remember-me" id="remember-me" class="form-check-input"
                    ${cookie.rememberMe!=null?"checked":"" } />
                <label for="remember-me" class="form-check-label">id 기억하기</label>
            </div>
        </div>
        <div  class="d-flex justify-content-end">
          <div id="findpw" >비밀번호 찾기</div>
        </div>
        
        <div class="buttons">
            <button type="submit">로그인</button>
            <button type="button" onclick="window.location.href='${root }/member/gotoregisterpage'">회원가입</button>
          </div>
      </form>
    </div>
    


   <div class="modal fade" tabindex="-1" role="dialog" id="modalPW">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content rounded-4 shadow">
      <div class="modal-header p-5 pb-4 border-bottom-0">
        <h1 class="fw-bold mb-0 fs-2">비밀번호 찾기</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>

      <div class="modal-body p-5 pt-0">
        <div class="form-floating mb-3">
          <input type="text" class="form-control rounded-3" id="findId" placeholder="아이디 입력">
          <label for="findId">아이디</label>
        </div>
        <div class="form-floating mb-3">
          <input type="text" class="form-control rounded-3" id="findName" placeholder="이름 입력">
          <label for="findName">이름</label>
        </div>
        <div class="form-floating mb-3">
          <input type="email" class="form-control rounded-3" id="findEmail" placeholder="이메일 입력">
          <label for="findEmail">이메일</label>
        </div>

        <!-- 비밀번호 찾기 버튼 -->
        <button class="w-100 mb-2 btn btn-lg rounded-3 btn-primary" id="findPwBtn">비밀번호 찾기</button>

        <!-- 결과 표시 -->
        <div id="passwordResult" class="mt-3 text-danger fw-bold"></div>

        <small class="text-body-secondary">
          회원가입 시 입력한 아이디, 이름과 이메일을 입력해주세요.
        </small>
      </div>
    </div>
  </div>
</div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

    <script src="${root }/assets/js/register.js"></script>
    <script src="${root }/assets/js/loginmodal.js"></script>
    <script>
    document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("findPwBtn").addEventListener("click", async function () {
            var userId = document.getElementById("findId").value.trim();
            var userName = document.getElementById("findName").value.trim();
            var userEmail = document.getElementById("findEmail").value.trim();

            if (!userId || !userName || !userEmail) {
                document.getElementById("passwordResult").innerText = "모든 필드를 입력하세요.";
                return;
            }

            // AJAX 요청 보내기
            const params = { findid: userId, findname: userName, findemail: userEmail };
            const password = await getFetch("${root }/member?action=findpw", "POST", params);

            // 결과 표시
            if (password && password !== "NOT_FOUND") {
                document.getElementById("passwordResult").innerText = "비밀번호: " + password;
            } else {
                document.getElementById("passwordResult").innerText = "일치하는 정보가 없습니다.";
            }
        });
    });

    // getFetch 함수
    async function getFetch(url, method = "GET", body = null) {
        const options = {
            method,
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        };

        if (body) {
            options.body = new URLSearchParams(body).toString();
        }

        try {
            const response = await fetch(url, options);
            if (!response.ok) throw new Error("서버 응답 오류");
            return await response.text(); // 서버에서 받은 텍스트 데이터 반환
        } catch (error) {
            console.error("Fetch Error:", error);
            return null;
        }
    }



    </script>
</body>
</html>