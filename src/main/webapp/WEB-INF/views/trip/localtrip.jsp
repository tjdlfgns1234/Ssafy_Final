<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="com.ssafy.dto.AttractionDTO" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script
        type="text/javascript"
        src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=843969214838bd4993b8e9d512a2abfa&libraries=services,clusterer"
      ></script>
</head>
<body>
	    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>

<div class="container">
      <div style="height: 70px"></div>
      <h4 class="my-4 fw-bold display-5 text-center">지역별 여행지!</h4>

      <!-- 지도 -->
      <form action="${root }/trip/getinfos" method="post">
        <label for="">지역 코드 기반의 서비스(Enjoy Trip): </label>
        <select id="areaCode" name="areaCode">
          <option value="" selected disabled>시도 선택</option>
          <c:if test="${!empty areaCode} ">
          <option value="${areaCode }">fff</option>
          </c:if>
        </select>
        <select id="sigunguCode" name="sigunguCode">
          <option value="" selected disabled>시군구 선택</option>
        </select>
        <select id="btn_trip" name="contentType">
          <option value="" selected disabled>관광타입 선택</option>
          <option value="12">관광지</option>
          <option value="14">문화시설</option>
          <!-- <option value="15">축제공연행사</option>
          <option value="25">여행코스</option>
          <option value="28">레포츠</option> -->
          <option value="32">숙박</option>
          <option value="38">카페</option>
          <option value="39">음식점</option>
        </select>
        <input id="btn_trip_search" type="submit" value="관광지_조회"/>
      </form>
	  <div>
		  <button onclick="resetPath()">경로 초기화</button>
		  <button onclick="findPath()">경로 찾기</button>
		  <p>
		    시작: <span id="start-coord">-</span> /
		    도착: <span id="end-coord">-</span>
		  </p>
	  </div>
			  
      <div id="map" style="width: 1300px; height: 1300px"></div>
      
      <script>
        // 지도 기본 설정
        var mapContainer = document.getElementById("map");
		var mapOption = {
		    center: new kakao.maps.LatLng(37.5665, 126.978), // Example center (Seoul City Hall)
		    level: 5,
		};
		var map = new kakao.maps.Map(mapContainer, mapOption);
		
		// Create the marker clusterer with your desired options.
		var clusterer = new kakao.maps.MarkerClusterer({
		    map: map,
		    averageCenter: true, // Use average of marker positions as the cluster center.
		    minLevel: 6,
		    gridSize: 30 // Adjust gridSize to control clustering behavior.
		});

        // 인포윈도우 (마커 클릭 시 장소명 표시)
        var infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });

        // 마커 배열(기존 마커 지우기용)
        var markers = [];
		let list = new Array();
        // "관광지 조회" 버튼 클릭 시
		if (`${!empty list }`) {
			<c:forEach var="data" items="${list}">
			    <%
			        // Retrieve the current data object from the page context.
			        // Make sure to cast it to the correct type.
			        AttractionDTO currentPlace = (AttractionDTO) pageContext.getAttribute("data");
			    %>
			    list.push({
			        "lati": "${data.lati}",
			        "longi": "${data.longi}",
			        "place": "<%= StringEscapeUtils.escapeEcmaScript(currentPlace.getTitle()) %>",
			        "addr": "<%= StringEscapeUtils.escapeEcmaScript(currentPlace.getAddr1()) %>",
			        "homepage": "<%= StringEscapeUtils.escapeEcmaScript(currentPlace.getHomepage()) %>",
			        "firstimg": "<%= StringEscapeUtils.escapeEcmaScript(currentPlace.getFirstimg()) %>"
			    });
			</c:forEach>

			console.log("exists!" + list.length);
		}
			//removeMarkers();
			
		function removeMarkers() {
		    for (var i = 0; i < markers.length; i++) {
		        markers[i].setMap(null);
		    }
		    markers = [];
		    clusterer.clear(); // Clear markers from the clusterer.
		}
		
		function updateMarkers() {
		    removeMarkers();
		    let bounds = map.getBounds();
		    
		    for (let e of list) {
		        let position = new kakao.maps.LatLng(Number(e.lati), Number(e.longi));
		        bounds.extend(position);
		        if (bounds.contain(position)) {
		            let marker = new kakao.maps.Marker({ position: position });
		            markers.push(marker);

		            clusterer.addMarker(marker);
		            
		            attachEventToMarker(e, marker);
		        }
		    }
		    
		   //map.setCenter(bounds.getCenter());
		    return bounds;
		}
		
		if (list.length != 0) map.setCenter(new kakao.maps.LatLng(list[0].lati, list[0].longi));
		let bounds = updateMarkers();
		//map.setCenter(bounds.getCenter());
		
		kakao.maps.event.addListener(map, 'idle', function() {
		    updateMarkers();
		});
      
        // 마커 생성
        function attachEventToMarker(place, marker) {

          //markers.push(marker);
          kakao.maps.event.addListener(map, "click", function () {
              infowindow.close();
          });

			
          // 마커 클릭 이벤트 - 인포윈도우에 장소명 표시
          kakao.maps.event.addListener(marker, "click", function () {
        	    let placeName = place.place ? place.place : "";
        	    let homepageHtml = (place.homepage && place.homepage !== "null") ? "링크 : " + place.homepage : "";
        	    
        	    let imageHtml = "";
        	    if (place.firstimg) {
        	        imageHtml = '<img src="' + place.firstimg + '" style="max-width:200px; height:auto;" />';
        	    }
        	    
        	    var content =
        	        '<div style="padding:5px;font-size:12px; border:1px solid #fff; background-color:#fff;">' +
        	            '<div>' + placeName + '</div>' +
        	            homepageHtml +
        	            imageHtml +
        	        '</div>';
        	    
        	    infowindow.setContent(content);
        	    infowindow.open(map, marker);
        	});

        }
        let startPoint = null;
        let endPoint = null;
        let startMarker = null;
        let endMarker = null;

        // Right-click to set start/end
        kakao.maps.event.addListener(map, 'rightclick', function(mouseEvent) {
            let latlng = mouseEvent.latLng;

            if (!startPoint) {
                startPoint = latlng;
                if (startMarker) startMarker.setMap(null);
                startMarker = new kakao.maps.Marker({
                    position: startPoint,
                    map: map,
                    title: "출발지",
                    image: new kakao.maps.MarkerImage(
                        "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png",
                        new kakao.maps.Size(40, 42),
                        { offset: new kakao.maps.Point(13, 42) }
                    )
                });
                document.getElementById("start-coord").innerText = latlng.getLat() + ", " + latlng.getLng();
            } else if (!endPoint) {
                endPoint = latlng;
                if (endMarker) endMarker.setMap(null);
                endMarker = new kakao.maps.Marker({
                    position: endPoint,
                    map: map,
                    title: "도착지",
                    image: new kakao.maps.MarkerImage(
                        "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png",
                        new kakao.maps.Size(40, 42),
                        { offset: new kakao.maps.Point(13, 42) }
                    )
                });
                document.getElementById("end-coord").innerText = latlng.getLat() + ", " + latlng.getLng();
            } else {
                alert("경로를 초기화 해주세요 (출발/도착 모두 선택됨)");
            }
        });

        // Reset path
        function resetPath() {
            if (startMarker) startMarker.setMap(null);
            if (endMarker) endMarker.setMap(null);
            startPoint = null;
            endPoint = null;
            document.getElementById("start-coord").innerText = "-";
            document.getElementById("end-coord").innerText = "-";
            if (routeLine) routeLine.setMap(null);
        }

        // Call backend to find path
        let routeLine = null;

        function findPath() {
            if (!startPoint || !endPoint) {
                alert("출발지와 도착지를 모두 지정해주세요 (우클릭)");
                return;
            }
			let url = `${root}/trip/route?fromLat=\${startPoint.getLat()}&fromLon=\${startPoint.getLng()}&toLat=\${endPoint.getLat()}&toLon=\${endPoint.getLng()}`;
			console.log("🔍 Final fetch URL:", url);
			
            fetch(url)
                .then(res => res.json())
                .then(data => {
					//console.log(data);
                	//console.log(startPoint.getLat());
                    if (routeLine) routeLine.setMap(null);

                    const path = data.map(p => new kakao.maps.LatLng(p.lat, p.lng));

                    routeLine = new kakao.maps.Polyline({
                        map: map,
                        path: path,
                        strokeWeight: 5,
                        strokeColor: '#FF0000',
                        strokeOpacity: 0.8,
                        strokeStyle: 'solid'
                    });

                    map.setCenter(path[Math.floor(path.length / 2)]);
                })
                .catch(e => {
                    console.error(e);
                    alert("경로를 찾는 중 오류 발생");
                });
        }

      </script>
      
    </div>

    <!--서비스 ID 설정-->
    <script
      type="text/javascript"
      src="https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=D665C6F9-5FB1-3E8E-B17D-26F0225847DC"
    defer></script>
    <script src="${root }/assets/js/keys.js"></script>
    <script src="${root }/assets/js/initfetcher.js"></script>
    <script>
      /* const init = async () => {
        // updateSido();
        areaCode1();
      };
      init(); */
    </script>
    <%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
    
</body>
</html>