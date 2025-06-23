<template>
  <div class="home-page">
    <!-- 1) 웰컴 오버레이 -->
    <div class="welcome-overlay">
      <div class="welcome-content">
        <h1>한국 여행에 오신 여러분을 진심으로 환영합니다!</h1>
        <p>
          한국에서의 다음 모험을 위해 최고의 여행지와 축제, 그리고 완벽한 여행 일정을 만나보세요!
        </p>
        <div class="welcome-buttons">
          <router-link to="/destinations" class="explore-button">지역 여행하기</router-link>
          <router-link to="/hotspots" class="festivals-button">축제 & 이벤트 찾기</router-link>
        </div>
      </div>
    </div>

    <!-- 2) Top 3 게시글 -->
    <div class="featured-destinations" v-if="topPosts.length">
      <h2>Top 3 게시글</h2>
      <div class="destinations-grid">
        <div
          v-for="post in topPosts"
          :key="post.postId"
          class="destination-card"
          @click="goToDetail(post.postId)"
        >
          <h3 class="destination-title">{{ post.title }}</h3>
          <p class="post-excerpt">{{ post.content }}</p>
          <p class="post-actions">❤️ {{ post.likes }} 💬 {{ post.commentCount }}</p>
        </div>
      </div>
    </div>
    <div class="featured-destinations" v-else>
      <h2>Top 3 게시글</h2>
      <p>게시글을 불러오는 중입니다...</p>
    </div>

    <!-- 3) 오늘의 축제 캐러셀 -->
    <div class="todays-festivals" v-if="todaysFestivals.length > 0">
      <h2>오늘의 축제</h2>

      <div class="festival-carousel">
        <button class="carousel-nav prev" @click="prevFestival">‹</button>
        <div class="carousel-slide">
          <div class="festival-card">
            <img
              :src="todaysFestivals[currentFestival].image"
              :alt="todaysFestivals[currentFestival].name"
            />
          </div>
        </div>
        <button class="carousel-nav next" @click="nextFestival">›</button>
      </div>

      <div class="carousel-indicators">
        <span
          v-for="(_, idx) in todaysFestivals"
          :key="idx"
          :class="{ active: idx === currentFestival }"
          @click="currentFestival = idx"
        />
      </div>
    </div>
    <div class="todays-festivals" v-else>
      <h2>오늘의 축제</h2>
      <p>오늘 진행되는 축제가 없습니다. 내일 다시 확인해 보세요!</p>
    </div>

    <!-- 4) 토스트 메시지 -->
    <div :class="['toast-message', { show: toastVisible }]">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// — 오늘의 축제 캐러셀 관련 상태 & 함수 —
const todaysFestivals = ref([])
const currentFestival = ref(0)

// 오늘 날짜를 YYYYMMDD로
function getTodayYYYYMMDD() {
  const d = new Date()
  return `${d.getFullYear()}${String(d.getMonth() + 1).padStart(2, '0')}${String(d.getDate()).padStart(2, '0')}`
}

function prevFestival() {
  const len = todaysFestivals.value.length
  currentFestival.value = (currentFestival.value - 1 + len) % len
}
function nextFestival() {
  const len = todaysFestivals.value.length
  currentFestival.value = (currentFestival.value + 1) % len
}

async function fetchTodaysFestivals() {
  const serviceKey =
    ''
  const dateStr = getTodayYYYYMMDD()

  const params = {
    serviceKey,
    PageNo: '1',
    numOfrows: '15',
    serviceTp: 'B',
    from: dateStr,
    // to: dateStr,
  }

  try {
    let url = 'https://apis.data.go.kr/B553457/nopenapi/rest/publicperformancedisplays/period'

    let config = {
      responseType: 'text',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: undefined,
      },
      params,
    }

    const newAxios = axios.create()

    const res = await newAxios.get(url, config)
    // console.log('오늘의 축제 API 응답:', res.data)
    const xml = new DOMParser().parseFromString(res.data, 'application/xml')
    const items = Array.from(xml.getElementsByTagName('item'))
    todaysFestivals.value = items.map((item) => ({
      id: item.querySelector('seq')?.textContent || '',
      name: item.querySelector('title')?.textContent || '',
      description: item.querySelector('serviceName')?.textContent || '',
      image: item.querySelector('thumbnail')?.textContent || '',
    }))
    await nextTick()
  } catch (e) {
    console.error('오늘의 축제 API 오류:', e)
    todaysFestivals.value = []
  }
}

// — Top3 게시글 로직 —
const topPosts = ref([])
const auth = useAuthStore()
const router = useRouter()

// axios 기본 설정
async function fetchTopPosts() {
  try {
    const res = await axios.get('http://192.168.205.81:8080/api/v1/posts/top3')
    const raw = res.data.posts ?? res.data
    topPosts.value = raw.map((p) => ({
      postId: p.postId,
      title: p.title,
      content: p.content,
      likes: p.likes,
      commentCount: p.commentCount,
    }))
  } catch (err) {
    console.error('Top3 게시글 오류:', err)
  }
}

const toastMessage = ref('')
const toastVisible = ref(false)
function showToast(msg) {
  toastMessage.value = msg
  toastVisible.value = true
  setTimeout(() => (toastVisible.value = false), 1500)
}

function goToDetail(postId) {
  if (!auth.isAuthenticated) {
    showToast('로그인이 필요합니다')
    return
  }
  router.push({ name: 'TravelInfoDetail', params: { postId } })
}

// — 마운트 시 API 호출 —
onMounted(() => {
  fetchTodaysFestivals()
  fetchTopPosts()
})
</script>

<style scoped>
.home-page {
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  min-height: calc(100vh - var(--topbar-height));
}

/* 웰컴 오버레이, 버튼 등… (생략) */

/* 오늘의 축제 */
.todays-festivals {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: var(--shadow);
  margin-bottom: 20px;
}
.todays-festivals h2 {
  color: var(--primary-color);
  margin-bottom: 16px;
}

/* 캐러셀 */
.festival-carousel {
  display: flex;
  align-items: center;
  position: relative;
}
.carousel-nav {
  background: var(--primary-color);
  color: white;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
}
.carousel-slide {
  flex: 1;
  display: flex;
  justify-content: center;
  overflow: hidden;
}
.festival-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}
.festival-card img {
  width: 600px !important;
  height: 600px !important;
  /* max-width: 600px !important;
  max-height: 600px !important; */
}

/* 도트 */
.carousel-indicators {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}
.carousel-indicators span {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin: 0 4px;
  border-radius: 50%;
  background: #ccc;
  cursor: pointer;
}
.carousel-indicators span.active {
  background: var(--primary-color);
}
.learn-more {
  display: inline-block;
  padding: 6px 12px;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 0.9rem;
}

.learn-more:hover {
  background-color: #004c8e;
}

.toast-message {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.5s ease-in-out;
  z-index: 9999;
}

.toast-message.show {
  opacity: 1;
}

.featured-destinations {
  margin-bottom: 40px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.featured-destinations h2 {
  color: var(--primary-color);
  margin-bottom: 20px;
}

.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.destination-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  padding: 20px;
  cursor: pointer;
}

.destination-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.post-date {
  color: var(--text-light);
  font-size: 0.9rem;
}

.destination-title {
  margin: 0 0 10px;
  color: var(--primary-color);
}

.post-excerpt {
  color: var(--text-light);
  font-size: 0.9rem;
  max-height: 3em;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 15px;
}

.post-actions {
  display: flex;
  gap: 15px;
}

.like-button,
.comment-button,
.share-button {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-light);
  transition: color 0.2s ease;
}

.like-button:hover,
.comment-button:hover,
.share-button:hover {
  color: var(--primary-color);
}

.home-page {
  padding: 20px;
  height: calc(100vh - var(--topbar-height));
  overflow-y: auto;
  background-color: rgba(255, 255, 255, 0.9);
  position: relative;
  z-index: 2;
}
.featured-destinations {
  margin-bottom: 40px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.featured-destinations h2 {
  color: var(--primary-color);
  margin-bottom: 20px;
}

.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.destination-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  padding: 20px;
}

.destination-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.destination-card h3 {
  margin: 0 0 10px;
  color: var(--primary-color);
}

.post-excerpt {
  color: var(--text-light);
  font-size: 0.9rem;
  max-height: 3em;
  overflow: hidden;
  text-overflow: ellipsis;
}
.welcome-overlay {
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  text-align: center;
  padding: 60px 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.welcome-content {
  max-width: 800px;
  margin: 0 auto;
}

.welcome-content h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.welcome-content p {
  font-size: 1.2rem;
  margin-bottom: 30px;
}

.welcome-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.explore-button,
.festivals-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 12px 25px;
  font-size: 1.1rem;
  font-weight: bold;
  text-decoration: none;
  transition: background-color 0.2s ease;
}

.explore-button:hover,
.festivals-button:hover {
  background-color: #004c8e;
}

.featured-destinations {
  margin-bottom: 40px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.featured-destinations h2 {
  color: var(--primary-color);
  margin-bottom: 20px;
}

.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.destination-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.destination-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.destination-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.destination-card h3 {
  padding: 15px;
  margin: 0;
  color: var(--primary-color);
}

.destination-card p {
  padding: 0 15px 15px 15px;
  margin: 0;
  color: var(--text-light);
}

.learn-more {
  display: block;
  padding: 10px 15px;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
  text-align: center;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.learn-more:hover {
  background-color: #004c8e;
}

.todays-festivals {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 20px;
  margin-bottom: 20px;
}

.todays-festivals h2 {
  color: var(--primary-color);
  margin-bottom: 20px;
}

.festival-card {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.festival-card img {
  width: 150px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.festival-card h3 {
  margin: 0;
  color: var(--primary-color);
}

.festival-card p {
  margin: 5px 0;
  color: var(--text-light);
}

@media (max-width: 768px) {
  .welcome-content h1 {
    font-size: 2rem;
  }

  .welcome-content p {
    font-size: 1rem;
  }

  .welcome-buttons {
    flex-direction: column;
    align-items: center;
  }

  .explore-button,
  .festivals-button {
    width: 100%;
  }

  .destinations-grid {
    grid-template-columns: 1fr;
  }

  .festival-card {
    flex-direction: column;
    text-align: center;
  }

  .festival-card img {
    width: 100%;
    height: auto;
  }
}
</style>
