import axios from './axiosConfig'

const API_URL = import.meta.env.VUE_APP_API_URL || 'http://192.168.205.81:8080'

export const customPlaceService = {
  async getCustomPlaces(userId) {
    try {
      const response = await axios.get(`${API_URL}/api/v1/userplaces/user/${userId}`)
      console.log('Custom places fetched successfully:', response.data)
      return response.data
    } catch (error) {
      console.error('Error fetching custom places:', error)
      throw new Error(error.response?.data?.message || 'Failed to fetch custom places')
    }
  },

  async createCustomPlace(placeData) {
    try {
      // Convert to server format
      const serverData = {
        name: placeData.name,
        memo: placeData.memo,
        latitude: placeData.latitude,
        longitude: placeData.longitude,
        userId: placeData.userId,
      }

      const response = await axios.post(`${API_URL}/api/v1/userplaces`, serverData)
      console.log('Custom place created successfully:', response.data)

      // Server gives object in place
      const createdPlace = response.data.place
      return {
        ...createdPlace,
        id: createdPlace.placeId || createdPlace.id || Date.now(), // Use placeId as primary ID
        place_id: `${createdPlace.placeId || createdPlace.id || Date.now()}`,
        content_id: `${createdPlace.placeId || createdPlace.id || Date.now()}`,
      }
    } catch (error) {
      console.error('Error creating custom place:', error)
      throw new Error(error.response?.data?.message || 'Failed to create custom place')
    }
  },

  async updateCustomPlace(placeId, placeData) {
    try {
      // Convert to server format
      const serverData = {
        name: placeData.name,
        memo: placeData.memo,
        latitude: placeData.latitude,
        longitude: placeData.longitude,
        userId: placeData.userId,
      }

      const response = await axios.put(`${API_URL}/api/v1/userplaces/${placeId}`, serverData)
      console.log('Custom place updated successfully:', response.data)
      return response.data
    } catch (error) {
      console.error('Error updating custom place:', error)
      throw new Error(error.response?.data?.message || 'Failed to update custom place')
    }
  },

  async deleteCustomPlace(placeId) {
    try {
      const response = await axios.delete(`${API_URL}/api/v1/userplaces/${placeId}`)
      console.log('Custom place deleted successfully:', response.data)
      return response.data
    } catch (error) {
      console.error('Error deleting custom place:', error)
      throw new Error(error.response?.data?.message || 'Failed to delete custom place')
    }
  },
}
