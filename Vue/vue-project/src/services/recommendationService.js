import axios from 'axios'

const API_URL = import.meta.env.VUE_APP_API_URL || 'http://192.168.205.81:8080'

class recommendationService {
  async optimizeRoute(routesData) {
    try {
      const response = await axios.post(`${API_URL}/api/v1/optimize/optimize-route`, routesData)
      console.log('Route optimization completed successfully:', response.data)
      return response.data
    } catch (error) {
      console.error('Error optimizing route:', error)
      throw new Error(error.response?.data?.message || 'Failed to optimize route')
    }
  }

  async recommmendRouteAI(routesData) {
    try {
      const response = await axios.post(`${API_URL}/api/v1/route/recommend`, routesData)
      console.log('Route optimization completed successfully:', response.data)
      return response.data
    } catch (error) {
      console.error('Error optimizing route:', error)
      throw new Error(error.response?.data?.message || 'Failed to optimize route')
    }
  }

  // 경로 최적화 결과를 파싱하는 유틸리티 함수
  parseOptimizationResult(result) {
    if (!result || !result.dayRoutes) {
      throw new Error('Invalid optimization result format')
    }

    return result.dayRoutes.map((dayRoute) => {
      return {
        day: dayRoute.day,
        visits: dayRoute.visits.map((visit) => ({
          locationId: visit.locationId,
          order: visit.order,
        })),
      }
    })
  }

  // 총 이동 시간 계산 함수
  calculateTotalTravelTime(segments) {
    if (!segments || segments.length === 0) return null

    let totalDuration = 0
    segments.forEach((segment) => {
      if (segment.travelTime && segment.travelTime.duration) {
        totalDuration += segment.travelTime.duration
      }
    })

    return {
      duration: totalDuration,
      text: this.formatDuration(totalDuration),
    }
  }

  // 시간 포맷팅 함수
  formatDuration(seconds) {
    if (!seconds) return 'N/A'

    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)

    if (hours === 0) {
      return `${minutes}분`
    } else {
      return `${hours}시간 ${minutes}분`
    }
  }
}

export default new recommendationService()
