<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="jakarta.tags.core"%>
    <c:set var="root" value="${pageContext.request.contextPath }" />
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
      crossorigin="anonymous"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css"
    />
    
    <style>
       @import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');
        div {
            font-family: "Jua";
        }
        .hide {
        display: none !important;
        }
        /* .custom-navbar {
    background-color: linear-gradient(circle,  #6f42c1,  #c03d3d); /* 원하는 색상 코드로 변경 
  } */
        </style>
</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-light fixed-top" style="background: linear-gradient(to right, #1e88de, #cfd6de);">
        <nav class="container-xxl bd-gutter flex-wrap flex-lg-nowrap" aria-label="Main navigation">
          <a class="navbar-brand text-primary fw-bold" href="${root }/index">
            <img src="./assets/img/enjoytrip_logo.png" alt="" width="60" />
            <span style="color: rgb(89, 218, 227)">SSAFY  Trip</span>
          </a>
          <button
            class="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse text-light" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-lg=0 ">
              <li class="nav-item">
                <a class="nav-link" aria-current="page" href="${root }/trip">지역별여행지</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" aria-current="page" href="${root }/trip/mytripplan.jsp">나의여행계획</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" aria-current="page" href="${root }/trip/hotplace.jsp">핫플자랑하기</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" aria-current="page" href="${root }/trip/sharetrip.jsp">여행정보공유</a>
              </li>
            </ul>
            <!-- 로그인 전 -->
            <c:if test="${empty loginUser }">
	            <ul class="navbar-nav mb-2 me-2 mb-lg-0">
	              <li class="nav-item">
	                <a class="nav-link" aria-current="page" href="${root }/member/gotoregisterpage">회원가입</a>
	              </li>
	              <li class="nav-item">
	                <a class="nav-link" aria-current="page" href="${root }/member/gotologinpage">로그인</a>
	              </li>
	            </ul>
            </c:if>
            <c:if test="${!empty loginUser }">
            
            <!-- 로그인 후 -->
            <ul class="navbar-nav mb-2 me-2 mb-lg-0">
              <li class="nav-item">
                <a class="nav-link " id="logout" aria-current="page" href="${root }/member/logout">로그아웃</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" aria-current="page" href="${root }/member/mypage?id=${loginUser.id}">마이페이지</a>
              </li>
              
            </ul>
             </c:if>
            
          </div>
        </nav>
      </nav>
      <!-- 상단 navbar end -->
</body>
</html>