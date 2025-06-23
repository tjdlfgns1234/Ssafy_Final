<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    
</head>
<body>
	    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>
	    <div class="container">
        <div class="px-4 py-5 my-5 text-center">
          <img src="./assets/img/꿈돌이.jpg" alt="" height="500px"/>
        </div>
        <div class="container text-center mt-4">
            <hr class="my-4">
            <h2 class="fw-bold">지역별 여행지</h2>
            <p>지역별 여행지 확인</p>
            <a href="${root }/member/localtrip.html" class="btn btn-info">보러 가기</a>
        </div>
        <div class="container text-center mt-4">
            <hr class="my-4">
            <h2 class="fw-bold">나만의 여행 계획!</h2>
            <p>여행 경로, 숙박, 관광지, 예상 금액 등 나만의 멋진 계획을 공유해 주세요!</p>
            <a href="${root }/member/mytripplan.html" class="btn btn-info">보러 가기</a>
        </div>
        <div class="container text-center mt-4">
            <hr class="my-4">
            <h2 class="fw-bold">핫플 자랑하기</h2>
            <p>나만 알고 있는 핫플 보러가기!!!</p>
            <a href="${root }/member/hotplace.html" class="btn btn-info">보러 가기</a>
        </div>
        <div class="alert bg-primary opacity-75 mt-3 text-center fs-1 fw-bold" role="alert">Enjoy!!! Trip은 여러분의 많은 참여를 기다립니다.</div>
        <div id="map" style="width: 100%; height: 400px"></div>
        </div>
    </div>
	    
	    <%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
	
</body>
</html>