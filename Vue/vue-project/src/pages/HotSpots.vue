<template>
  <div class="hotspots-page">
    <!-- 지도 -->
    <div class="map-container" ref="mapContainer"></div>

    <!-- 사이드바 -->
    <ResizableSidebar
      :isOpen="isSidebarOpen"
      :width="sidebarWidth"
      title="축제 & 이벤트"
      @toggle="toggleSidebar"
      @resize="handleResize"
      :minWidth="300"
      :maxWidth="600"
    >
      <div class="hotspots-container">
        <div class="hotspots-header">
          <h2>축제 & 이벤트</h2>
          <p class="header-description">한국 전역의 다양한 축제와 이벤트를 만나보세요</p>
        </div>
        <!-- 캐러셀 -->
        <div class="festivals-carousel">
          <div class="carousel-container" ref="carouselContainer">
            <div
              v-for="(festival, idx) in festivals"
              :key="festival.id"
              class="festival-card"
              :class="{ active: idx === currentIndex }"
              @click="selectFestival(idx)"
            >
              <div class="festival-image">
                <img :src="festival.image" :alt="festival.name" @error="handleImageError" />
                <div class="festival-status" :class="festival.status">
                  {{ getStatusText(festival.status) }}
                </div>
              </div>
              <div class="festival-content">
                <h3>{{ festival.name }}</h3>
                <div class="festival-location">
                  <span class="location-icon">📍</span>
                  {{ festival.location }}
                </div>
                <div class="festival-date">
                  <span class="date-icon">📅</span>
                  {{ formatDateRange(festival.startDate, festival.endDate) }}
                </div>
                <div class="festival-category">
                  <span class="category-tag">{{ festival.category }}</span>
                </div>
                <p class="festival-description">{{ festival.description }}</p>
              </div>
            </div>
          </div>
        </div>
        <!-- 캐러셀 컨트롤 -->
        <div class="carousel-controls">
          <button class="carousel-btn prev" @click="movePrev" :disabled="currentIndex === 0">
            ‹
          </button>
          <div class="carousel-indicators">
            <span
              v-for="(festival, idx) in festivals"
              :key="idx"
              class="indicator"
              :class="{ active: idx === currentIndex }"
              @click="selectFestival(idx)"
            ></span>
          </div>
          <button
            class="carousel-btn next"
            @click="moveNext"
            :disabled="currentIndex === festivals.length - 1"
          >
            ›
          </button>
        </div>
      </div>
    </ResizableSidebar>

    <!-- 사이드바 열기 버튼 -->
    <button
      v-if="!isSidebarOpen"
      class="open-sidebar-button"
      @click="toggleSidebar"
      aria-label="Open hotspots sidebar"
    >
      <span>핫스팟 쇼케이스</span>
    </button>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted, nextTick } from 'vue'
import ResizableSidebar from '../components/layout/ResizableSidebar.vue'

const isSidebarOpen = ref(true)
const sidebarWidth = ref(400)
const mapContainer = ref(null)
const carouselContainer = ref(null)
const map = ref(null)
const markers = ref([])
const infoWindows = ref([])

const festivals = ref([])
const currentIndex = ref(0)

// API 연동 (POST) — axios 사용
async function fetchFestivals() {
  // 1) 요청할 엔드포인트

  // 2) 쿼리 파라미터 객체

  const today = new Date()
  const yyyy = today.getFullYear()
  const mm = String(today.getMonth() + 1).padStart(2, '0')
  const dd = String(today.getDate()).padStart(2, '0')
  const from = `${yyyy}${mm}${dd}`

  // 1년 뒤 날짜 구하기
  const nextYear = new Date(today)
  nextYear.setFullYear(today.getFullYear() + 1)
  const toYyyy = nextYear.getFullYear()
  const toMm = String(nextYear.getMonth() + 1).padStart(2, '0')
  const toDd = String(nextYear.getDate()).padStart(2, '0')
  const to = `${toYyyy}${toMm}${toDd}`
  const myServiceKey

  const params = {
    serviceKey: myServiceKey, // 여기에 실제 키 할당
    PageNo: '1',
    numOfrows: '50',
    // keyword: '축제', // 검색어 (필요 시)
    // gpsxfrom : '124.10',  // 서울 기준
    // gpsyfrom: '33.11',  // 서울 기준
    // gpsxto:  '131.87',
    // gpsyto:   '38.61',
    serviceTp: 'A',
    from: from,
    to: to,
  }

  // 3) axios 호출용 config

  // 4) 실제 요청
  try {
    let url = 'https://apis.data.go.kr/B553457/nopenapi/rest/publicperformancedisplays/period'

    let config = {
      baseURL: '', // 절대경로로만 호출
      responseType: 'text', // XML을 문자열로 받기 위해
      headers: { 'Content-Type': 'application/x-www-form-urlencoded', Authorization: undefined },
      withCredentials: false, // 필요 시 true로 변경
      params, // 위에서 정의한 params
    }

    const newAxios = axios.create()

    const res = await newAxios.get(url, config)

    const xmlText = res.data
    const parser = new DOMParser()
    const xml = parser.parseFromString(xmlText, 'application/xml')
    const items = xml.getElementsByTagName('item')
    // console.log(res.data)
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const gpsX = item.querySelector('gpsX')?.textContent
      const gpsY = item.querySelector('gpsY')?.textContent
      // console.log(item);

      festivals.value.push({
        id: item.querySelector('seq')?.textContent,
        name: item.querySelector('title')?.textContent,
        location:
          item.querySelector('place')?.textContent || item.querySelector('area')?.textContent,
        startDate: formatApiDate(item.querySelector('startDate')?.textContent),
        endDate: formatApiDate(item.querySelector('endDate')?.textContent),
        category: item.querySelector('realmName')?.textContent,
        status: getFestivalStatus(
          item.querySelector('startDate')?.textContent,
          item.querySelector('endDate')?.textContent,
        ),
        description: item.querySelector('serviceName')?.textContent,
        image: item.querySelector('thumbnail')?.textContent,
        coordinates: gpsY && gpsX ? { lat: parseFloat(gpsY), lng: parseFloat(gpsX) } : null,
      })
      // console.log(festivals.value[i]);
    }
    //console.log(festivals.value);

    // festivals.value = items;
    currentIndex.value = 0
    await nextTick()
    updateMapMarkers()
  } catch (e) {
    console.error('API 호출 또는 파싱 오류:', e)
    festivals.value = []
  }
}

// 날짜 포맷 변환
function formatApiDate(dateStr) {
  if (!dateStr || dateStr.length !== 8) return ''
  return `${dateStr.slice(0, 4)}-${dateStr.slice(4, 6)}-${dateStr.slice(6, 8)}`
}

// 축제 상태 계산
function getFestivalStatus(startDate, endDate) {
  const now = new Date()
  const start = new Date(formatApiDate(startDate))
  const end = new Date(formatApiDate(endDate))
  if (now < start) return 'upcoming'
  if (now > end) return 'ended'
  return 'ongoing'
}

// 지도/마커/인포윈도우 관리
function updateMapMarkers() {
  // 기존 마커/인포윈도우 제거
  markers.value.forEach((m) => m.setMap(null))
  markers.value = []
  infoWindows.value.forEach((iw) => iw.close())
  infoWindows.value = []

  if (!window.google || !map.value) return

  festivals.value.forEach((festival, idx) => {
    const markerColor =
      festival.status === 'ongoing'
        ? '#4CAF50'
        : festival.status === 'upcoming'
          ? '#FF9800'
          : '#9E9E9E'

    const marker = new window.google.maps.Marker({
      position: festival.coordinates,
      map: map.value,
      title: festival.name,
      icon: {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: markerColor,
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 12,
      },
      label: {
        text: '🎪',
        color: 'white',
        fontWeight: 'bold',
      },
      animation: window.google.maps.Animation.DROP,
    })

    const infoWindow = new window.google.maps.InfoWindow({
      content: `
        <div style="padding: 5px; max-width: 300px;">
        <h3 style="margin-top: 0; color: #005BAC; font-size: 16px;">${festival.name}</h3>
        <img src="${festival.image}" alt="${festival.name} 포스터" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px; margin-bottom: 8px;">
        <p style="margin: 5px 0;"><strong>위치:</strong> ${festival.location}</p>
        <p style="margin: 5px 0;"><strong>기간:</strong> ${formatDateRange(festival.startDate, festival.endDate)}</p>
        <p style="margin: 5px 0;"><strong>카테고리:</strong> ${festival.category}</p>
        <p style="margin: 5px 0;"><strong>상태:</strong> <span style="color: ${markerColor}; font-weight: bold;">${getStatusText(festival.status)}</span></p>
        <p style="margin: 5px 0;">${festival.description}</p>
      </div>
      `,
    })

    marker.addListener('click', () => {
      infoWindows.value.forEach((iw) => iw.close())
      infoWindow.open(map.value, marker)
      selectFestival(idx, true)
    })

    markers.value.push(marker)
    infoWindows.value.push(infoWindow)
  })

  // 지도 영역 자동 맞춤
  if (markers.value.length > 0) {
    const bounds = new window.google.maps.LatLngBounds()
    markers.value.forEach((marker) => bounds.extend(marker.getPosition()))
    map.value.fitBounds(bounds)
    window.google.maps.event.addListenerOnce(map.value, 'idle', () => {
      if (map.value.getZoom() > 12) map.value.setZoom(12)
    })
  } else {
    map.value.setCenter({ lat: 36.2, lng: 127.9 })
    map.value.setZoom(7)
  }
}

// 캐러셀 이동 및 선택
function movePrev() {
  if (currentIndex.value > 0) selectFestival(currentIndex.value - 1)
}
function moveNext() {
  if (currentIndex.value < festivals.value.length - 1) selectFestival(currentIndex.value + 1)
}
function selectFestival(idx, fromMarker = false) {
  currentIndex.value = idx
  const festival = festivals.value[idx]
  // 지도 이동
  if (festival.coordinates && map.value && !fromMarker) {
    map.value.panTo(festival.coordinates)
    map.value.setZoom(13)
    // 마커 애니메이션
    if (markers.value[idx]) {
      markers.value[idx].setAnimation(window.google.maps.Animation.BOUNCE)
      setTimeout(() => markers.value[idx].setAnimation(null), 1200)
    }
    // 인포윈도우 열기
    infoWindows.value.forEach((iw) => iw.close())
    infoWindows.value[idx].open(map.value, markers.value[idx])
  }
  // 캐러셀 스크롤
  nextTick(() => {
    const card = carouselContainer.value?.children[idx]
    if (card) card.scrollIntoView({ behavior: 'smooth', inline: 'center' })
  })
}

// 날짜 범위 표시
function formatDateRange(startDate, endDate) {
  if (!startDate || !endDate) return ''
  const start = new Date(startDate)
  const end = new Date(endDate)
  const options = { year: 'numeric', month: 'short', day: 'numeric' }
  if (startDate === endDate) return start.toLocaleDateString('ko-KR', options)
  return `${start.toLocaleDateString('ko-KR', options)} ~ ${end.toLocaleDateString('ko-KR', options)}`
}

// 상태 텍스트
function getStatusText(status) {
  const map = { ongoing: '진행중', upcoming: '예정', ended: '종료' }
  return map[status] || status
}

// 이미지 에러 핸들러
function handleImageError(event) {
  event.target.src = '/placeholder.svg?height=300&width=400&text=Festival+Image'
}

// 사이드바 토글/리사이즈
function toggleSidebar() {
  isSidebarOpen.value = !isSidebarOpen.value
}
function handleResize(newWidth) {
  sidebarWidth.value = newWidth
}

// 지도 초기화
onMounted(async () => {
  await nextTick()
  // 지도 API가 이미 로드되어 있다는 전제
  if (mapContainer.value && window.google && window.google.maps) {
    map.value = new window.google.maps.Map(mapContainer.value, {
      center: { lat: 36.2, lng: 127.9 },
      zoom: 7,
      mapTypeId: window.google.maps.MapTypeId.ROADMAP,
    })
    fetchFestivals()
  } else {
    console.error('Google Maps API가 로드되지 않았습니다.')
  }
})
</script>

<style scoped>
/* 기존 스타일 그대로 사용 */
.hotspots-page {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
.map-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
  z-index: 1;
}
.hotspots-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}
.hotspots-header {
  padding: 20px;
  background-color: var(--primary-color, #005bac);
  color: white;
  text-align: center;
}
.header-description {
  margin: 0;
  opacity: 0.9;
  font-size: 0.9rem;
}
.festivals-carousel {
  flex: 1;
  overflow: hidden;
  padding: 20px;
}
.carousel-container {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding-bottom: 10px;
  scrollbar-width: none;
  -ms-overflow-style: none;
  gap: 20px;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding-bottom: 10px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.carousel-container::-webkit-scrollbar {
  display: none;
}
.festival-card {
  flex: 0 0 300px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  border: 2px solid transparent;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}
.festival-card.active {
  border-color: var(--primary-color, #005bac);
  box-shadow: 0 6px 16px rgba(0, 91, 172, 0.2);
}
.festival-image {
  position: relative;
  height: 180px;
  height: 180px;
  overflow: hidden;
}
.festival-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.festival-card:hover .festival-image img {
  transform: scale(1.05);
}
.festival-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
}
.festival-status.ongoing {
  background: #4caf50;
}
.festival-status.upcoming {
  background: #ff9800;
}
.festival-status.ended {
  background: #9e9e9e;
}
.festival-content {
  padding: 20px;
}
.festival-content h3 {
  margin: 0 0 10px 0;
  color: var(--primary-color, #005bac);
  font-size: 1.2rem;
}
.festival-location,
.festival-date {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #666;
}
.location-icon,
.date-icon {
  margin-right: 6px;
}
.festival-category {
  margin-bottom: 12px;
}
.category-tag {
  display: inline-block;
  background: #f0f3fa;
  color: var(--primary-color, #005bac);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}
.festival-description {
  color: #222;
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.carousel-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  gap: 15px;
}
.carousel-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: var(--primary-color, #005bac);
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.carousel-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.carousel-indicators {
  display: flex;
  gap: 8px;
  gap: 8px;
}
.indicator {
  width: 8px;
  height: 8px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ddd;
  cursor: pointer;
  transition: all 0.2s;
}
.indicator.active {
  background: var(--primary-color, #005bac);
  transform: scale(1.2);
}
.open-sidebar-button {
  position: fixed;
  top: 40px;
  left: 20px;
  background: var(--primary-color, #005bac);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 15px;
  font-weight: bold;
  cursor: pointer;
  z-index: 10;
  z-index: 10;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: background 0.2s;
}
.open-sidebar-button:hover {
  background: #004c8e;
}
@media (max-width: 768px) {
  .festival-card {
    flex: 0 0 250px;
  }
  .carousel-controls {
    padding: 15px;
  }
  .carousel-btn {
    width: 35px;
    height: 35px;
    font-size: 1rem;
  }

  .carousel-controls {
    padding: 15px;
  }

  .carousel-btn {
    width: 35px;
    height: 35px;
    font-size: 1rem;
  }
}
</style>
