import axios from './axiosConfig.js'

export const attractionService = {
  // 여행지 정보 조회
  async getAttractions(params = {}) {
    try {
      const queryParams = new URLSearchParams()

      if (params.areaCode) {
        queryParams.append('areaCode', params.areaCode)
      }
      if (params.sigunguCode) {
        queryParams.append('sigunguCode', params.sigunguCode)
      }
      if (params.contentType) {
        queryParams.append('contentType', params.contentType)
      }

      const response = await axios.get(`/api/v1/trip/infos?${queryParams.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error fetching attractions:', error)
      throw error
    }
  },
  async searchAttractions(keyword = null, contentTypeId = null) {
    try {
      const queryParams = new URLSearchParams()

      // 🔥 검색어가 있을 때만 추가
      if (keyword && keyword.trim()) {
        queryParams.append('keyword', keyword.trim())
      }

      // 카테고리 필터 추가
      if (contentTypeId) {
        queryParams.append('contentType', contentTypeId)
      }

      // 기본 파라미터들
      queryParams.append('numOfRows', '20')
      queryParams.append('pageNo', '1')

      const response = await axios.get(`/api/v1/trip/infos?${queryParams.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error searching attractions:', error)
      throw error
    }
  },
}
