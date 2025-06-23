<template>
  <div class="admin-page">
    <div class="page-header">
      <BackButton />
      <h1>관리자 대시보드</h1>
      <div class="refresh-control">
        <span>Auto-refresh: {{ autoRefresh ? 'On' : 'Off' }}</span>
        <label class="toggle-switch">
          <input type="checkbox" v-model="autoRefresh" />
          <span class="toggle-slider"></span>
        </label>
      </div>
    </div>

    <div class="metrics-grid">
      <!-- Server Status -->
      <div class="metrics-card">
        <div class="card-header">
          <h2>Server Status</h2>
        </div>
        <div class="card-content server-charts-container">
          <div class="server-chart-block">
            <h3>CPU Usage</h3>
            <canvas ref="cpuChartRef" class="server-chart-canvas"></canvas>
          </div>
          <div class="server-chart-block">
            <h3>RAM Usage</h3>
            <canvas ref="ramChartRef" class="server-chart-canvas"></canvas>
          </div>
          <div class="server-chart-block">
            <h3>Disk I/O</h3>
            <canvas ref="diskChartRef" class="server-chart-canvas"></canvas>
          </div>
        </div>
      </div>

      <!-- API Metrics -->
      <div class="metrics-card">
        <div class="card-header">
          <h2>API Metrics</h2>
        </div>
        <div class="card-content">
          <div class="metric-item" v-for="(m, key) in apiMetrics" :key="key">
            <h3>{{ titles[key] }}</h3>
            <div class="api-metrics">
              <div class="api-metric">
                <span class="metric-label">p50:</span>
                <span class="metric-value">{{ m.p50 }}ms</span>
              </div>
              <div class="api-metric">
                <span class="metric-label">p95:</span>
                <span class="metric-value">{{ m.p95 }}ms</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Metrics Card -->
      <div class="metrics-card">
        <div class="card-header">
          <h2>Database Metrics</h2>
        </div>
        <div class="card-content">
          <div class="metric-item">
            <h3>Members</h3>
            <div class="metric-count">{{ dbMetrics.memberCount }}</div>
          </div>
          <div class="metric-item">
            <h3>Posts</h3>
            <div class="metric-count">{{ dbMetrics.postsCount }}</div>
          </div>
          <div class="metric-item">
            <h3>Comments</h3>
            <div class="metric-count">{{ dbMetrics.commentsCount }}</div>
          </div>
          <div class="metric-item">
            <h3>Post Likes</h3>
            <div class="metric-count">{{ dbMetrics.postlikesCount }}</div>
          </div>
          <div class="metric-item">
            <h3>Comment Likes</h3>
            <div class="metric-count">{{ dbMetrics.commentlikesCount }}</div>
          </div>
          <div class="metric-item">
            <h3>User Places</h3>
            <div class="metric-count">{{ dbMetrics.userplacesCount }}</div>
          </div>
          <div class="metric-item">
            <h3>Travel Plans</h3>
            <div class="metric-count">{{ dbMetrics.travelplansCount }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { shallowRef, onMounted, onBeforeUnmount, watch, nextTick, ref } from 'vue'
import BackButton from '../components/common/BackButton.vue'
import axios from 'axios'
import Chart from 'chart.js/auto'

// -----------------------------
// 1) Reactive Data
// -----------------------------
const apiMetrics = ref({
  posts: { p50: 0, p95: 0 },
  members: { p50: 0, p95: 0 },
  comments: { p50: 0, p95: 0 },
  route: { p50: 0, p95: 0 },
})

// --- 새로 추가: DB 메트릭(DTO) ---
const dbMetrics = ref({
  memberCount: 0,
  postsCount: 0,
  commentsCount: 0,
  postlikesCount: 0,
  commentlikesCount: 0,
  userplacesCount: 0,
  travelplansCount: 0,
})

// API 타이틀 매핑
const titles = {
  posts: '전체 게시글 조회',
  members: '단일 회원 조회',
  comments: '해당 게시글 댓글 조회',
  route: 'AI 기반 경로 추천',
}

// Auto-refresh 토글
const autoRefresh = shallowRef(true)
let refreshInterval = null

// 서버 차트 refs & instances...
const cpuChartRef = shallowRef(null)
const ramChartRef = shallowRef(null)
const diskChartRef = shallowRef(null)
const cpuChart = shallowRef(null)
const ramChart = shallowRef(null)
const diskChart = shallowRef(null)
const serverHistory = shallowRef([])

// -----------------------------
// 2) Fetch Functions
// -----------------------------
async function fetchServerMetrics() {
  try {
    const API_BASE = 'http://192.168.205.81:8080'
    const api = axios.create({ baseURL: API_BASE })
    const { data } = await api.get('/api/v1/metrics/server')
    const now = new Date().toLocaleTimeString()
    serverHistory.value.push({
      time: now,
      cpu: Math.round(data.cpu),
      ram: Math.round(data.ram),
      disk: Math.round(data.disk),
    })
    if (serverHistory.value.length > 20) serverHistory.value.shift()

    updateChart(
      cpuChart.value,
      serverHistory.value.map((p) => p.time),
      serverHistory.value.map((p) => p.cpu),
    )
    updateChart(
      ramChart.value,
      serverHistory.value.map((p) => p.time),
      serverHistory.value.map((p) => p.ram),
    )
    updateChart(
      diskChart.value,
      serverHistory.value.map((p) => p.time),
      serverHistory.value.map((p) => p.disk),
    )
  } catch (e) {
    console.error('서버 메트릭 로드 실패', e)
  }
}

async function fetchApiMetrics() {
  try {
    const API_BASE = 'http://192.168.205.81:8080'
    const api = axios.create({ baseURL: API_BASE })
    const { data } = await api.get('/api/v1/metrics/apis')
    apiMetrics.value.posts = data.posts
    apiMetrics.value.members = data.members
    apiMetrics.value.comments = data.comments
    apiMetrics.value.route = data.route
  } catch (e) {
    console.error('API 메트릭 로드 실패', e)
  }
}

// --- 새로 추가: DB 메트릭 조회 ---
async function fetchDbMetrics() {
  try {
    const API_BASE = 'http://192.168.205.81:8080'
    const api = axios.create({ baseURL: API_BASE })
    const { data } = await api.get('/api/v1/metrics/all')
    // MetricsDTO 필드 매핑
    dbMetrics.value.memberCount = data.memberCount
    dbMetrics.value.postsCount = data.postsCount
    dbMetrics.value.commentsCount = data.commentsCount
    dbMetrics.value.postlikesCount = data.postlikesCount
    dbMetrics.value.commentlikesCount = data.commentlikesCount
    dbMetrics.value.userplacesCount = data.userplacesCount
    dbMetrics.value.travelplansCount = data.travelplansCount
  } catch (e) {
    console.error('DB 메트릭 로드 실패', e)
  }
}

// -----------------------------
// 3) Chart Utils
// -----------------------------
function updateChart(chart, labels, dataset) {
  if (!chart) return
  chart.data.labels = labels
  chart.data.datasets[0].data = dataset
  chart.update()
}

function initChart(refEl, label) {
  const canvas = refEl.value
  if (!canvas) return null
  const ctx = canvas.getContext('2d')
  return new Chart(ctx, {
    type: 'line',
    data: {
      labels: [],
      datasets: [{ label, data: [], fill: true, tension: 0.3 }],
    },
    options: {
      scales: { x: { display: false }, y: { beginAtZero: true, max: 100 } },
      plugins: { legend: { display: false } },
      animation: { duration: 500 },
    },
  })
}

// -----------------------------
// 4) Auto-refresh 관리
// -----------------------------
function restartAutoRefresh() {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
  if (autoRefresh.value) {
    // 초기 fetch
    fetchServerMetrics()
    fetchApiMetrics()
    fetchDbMetrics() // ← DB 메트릭도 주기적으로 갱신
    // 5초마다 리프레시
    refreshInterval = setInterval(() => {
      fetchServerMetrics()
      fetchApiMetrics()
      fetchDbMetrics()
    }, 5000)
  }
}

// -----------------------------
// 5) 생명주기 훅
// -----------------------------
onMounted(async () => {
  await nextTick()
  cpuChart.value = initChart(cpuChartRef, 'CPU Usage (%)')
  ramChart.value = initChart(ramChartRef, 'RAM Usage (%)')
  diskChart.value = initChart(diskChartRef, 'Disk I/O (%)')

  watch(autoRefresh, restartAutoRefresh, { immediate: true })
})

onBeforeUnmount(() => {
  if (refreshInterval) clearInterval(refreshInterval)
  cpuChart.value?.destroy()
  ramChart.value?.destroy()
  diskChart.value?.destroy()
})
</script>

<style scoped>
.admin-page {
  padding: 20px;
  height: calc(100vh - var(--topbar-height));
  overflow-y: auto;
  background-color: rgba(255, 255, 255, 0.9);
}
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 {
  margin-left: 15px;
  margin-right: auto;
  color: var(--primary-color);
}
.refresh-control {
  display: flex;
  align-items: center;
  gap: 10px;
}
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}
.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}
.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 20px;
}
.toggle-slider:before {
  position: absolute;
  content: '';
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}
input:checked + .toggle-slider {
  background-color: var(--primary-color);
}
input:checked + .toggle-slider:before {
  transform: translateX(20px);
}
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}
.metrics-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: var(--background-light);
  border-bottom: 1px solid var(--border-color);
}
.card-content {
  padding: 15px;
}
.metric-item {
  margin-bottom: 20px;
}
.metric-item:last-child {
  margin-bottom: 0;
}
.metric-item h3 {
  margin: 0 0 10px;
  font-size: 1rem;
}
.api-metrics {
  display: flex;
  gap: 20px;
}
.api-metric {
  background-color: var(--background-light);
  padding: 10px;
  border-radius: 4px;
  flex: 1;
}
.metric-label {
  font-weight: bold;
  margin-right: 5px;
}
.metric-value {
  color: var(--primary-color);
}
.user-count {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  color: var(--primary-color);
}
.server-charts-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.server-chart-block {
  width: 100%;
}
.server-chart-canvas {
  width: 100% !important;
  height: 150px !important;
}
</style>
