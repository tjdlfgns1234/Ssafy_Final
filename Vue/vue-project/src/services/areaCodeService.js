// 환경변수에서 API 키 가져오기
const API_KEY =
  import.meta.env.VITE_KOREA_TOURISM_API_KEY ||
  '+=='

export const areaCodeService = {
  // 시도 목록 조회
  async getSidoList() {
    try {
      const queryParams = new URLSearchParams({
        serviceKey: API_KEY,
        numOfRows: 120,
        pageNo: 1,
        MobileOS: 'ETC',
        MobileApp: 'Test',
        _type: 'json',
      })

      const response = await fetch(
        `https://apis.data.go.kr/B551011/KorService1/areaCode1?${queryParams.toString()}`,
      )
      const data = await response.json()

      if (data.response && data.response.body && data.response.body.items) {
        const items = data.response.body.items.item || []
        return items.map((item) => ({
          code: item.code,
          name: item.name,
        }))
      }

      return []
    } catch (error) {
      console.error('Error fetching sido list:', error)
      throw error
    }
  },

  // 시군구 목록 조회
  async getSigunguList(areaCode) {
    try {
      const queryParams = new URLSearchParams({
        serviceKey: API_KEY,
        numOfRows: 120,
        pageNo: 1,
        MobileOS: 'ETC',
        MobileApp: 'Test',
        _type: 'json',
        areaCode: areaCode,
      })

      const response = await fetch(
        `https://apis.data.go.kr/B551011/KorService1/areaCode1?${queryParams.toString()}`,
      )
      const data = await response.json()

      if (data.response && data.response.body && data.response.body.items) {
        const items = data.response.body.items.item || []
        return items.map((item) => ({
          code: item.code,
          name: item.name,
        }))
      }

      return []
    } catch (error) {
      console.error('Error fetching sigungu list:', error)
      throw error
    }
  },
}
