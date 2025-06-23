import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import { computed } from 'vue'

// Set base URL
axios.defaults.baseURL = 'http://192.168.205.81:8080'

// Setup request interceptor to add token
const setupInterceptors = () => {
  const auth = useAuthStore()
  const token = computed(() => auth.token || localStorage.getItem('accessToken'))

  axios.interceptors.request.use(
    (config) => {
      const t = token.value
      if (t) {
        config.headers.Authorization = `Bearer ${t}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )

  // Response interceptor for handling token expiration
  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      // Just return the error without automatic logout/redirect
      // Let individual services handle errors as needed
      return Promise.reject(error)
    },
  )
}

export { setupInterceptors }
export default axios
