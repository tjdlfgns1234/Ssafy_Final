import { defineStore } from 'pinia'
import axios from 'axios'

// ← 여기! 베이스 URL 변수로 분리
// const BASE_URL = "http://localhost:8080"
const BASE_URL = 'http://192.168.205.81:8080' // 실제 서버 주소로 변경
// axios 기본 설정
axios.defaults.baseURL = BASE_URL

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    user: null, // { id, name, email, role }
    isAuthenticated: false,
    isLoading: false,
    error: null,
  }),

  actions: {
    // 1) 로그인 → 토큰 발급 → 프로필 조회
    async login(id, pw) {
      this.isLoading = true
      this.error = null

      try {
        const loginRes = await axios.post(BASE_URL + '/api/v1/members/login', { id, pw })
        const token = loginRes.data.token

        this.token = token
        localStorage.setItem('token', token)
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

        const profileRes = await axios.get(BASE_URL + `/api/v1/members/${id}`)
        const m = profileRes.data.member

        this.user = {
          mno: m.mno,
          id: m.id,
          name: m.name,
          email: m.email,
          role: m.role,
        }
        this.isAuthenticated = true
        localStorage.setItem('user', JSON.stringify(this.user))

        return this.user
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.isLoading = false
      }
    },

    // 2) 새로고침 시 localStorage에서 복구 + 토큰 유효성 검증
    async checkAuth() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')

      if (token && user) {
        this.token = token
        this.user = JSON.parse(user)
        this.isAuthenticated = true
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

        try {
          // 토큰 유효성 검증을 위해 프로필 API 호출
          const id = this.user.id
          await axios.get(BASE_URL + `/api/v1/members/${id}`)
          // 200이면 아무것도 하지 않음 (유저 정보 유지)
        } catch (err) {
          // 실패(토큰 만료, 유효하지 않음 등) 시 localStorage 및 상태 초기화
          this.token = null
          this.user = null
          this.isAuthenticated = false
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          delete axios.defaults.headers.common['Authorization']
        }
      }
    },

    // 3) 로그아웃
    logout() {
      this.token = null
      this.user = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      delete axios.defaults.headers.common['Authorization']
    },
  },
})
