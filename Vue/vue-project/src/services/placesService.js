import axios from "./axiosConfig"

export const placesService = {
  // Get all places
  async getAllPlaces() {
    try {
      const response = await axios.get("/api/v1/places")
      return response.data
    } catch (error) {
      console.error("Error fetching places:", error)
      throw error
    }
  },

  // Get places by region
  async getPlacesByRegion(regionId) {
    try {
      const response = await axios.get(`/api/v1/places/region/${regionId}`)
      return response.data
    } catch (error) {
      console.error("Error fetching places by region:", error)
      throw error
    }
  },

  // Get place details
  async getPlaceDetails(placeId) {
    try {
      const response = await axios.get(`/api/v1/places/${placeId}`)
      return response.data
    } catch (error) {
      console.error("Error fetching place details:", error)
      throw error
    }
  },

  // Search places
  async searchPlaces(query, filters = {}) {
    try {
      const params = new URLSearchParams({
        q: query,
        ...filters,
      })
      const response = await axios.get(`/api/v1/places/search?${params}`)
      return response.data
    } catch (error) {
      console.error("Error searching places:", error)
      throw error
    }
  },

  // Get custom places for user
  async getCustomPlaces(userId) {
    try {
      const response = await axios.get(`/api/v1/places/custom/${userId}`)
      return response.data
    } catch (error) {
      console.error("Error fetching custom places:", error)
      throw error
    }
  },

  // Create custom place
  async createCustomPlace(placeData) {
    try {
      const response = await axios.post("/api/v1/places/custom", placeData)
      return response.data
    } catch (error) {
      console.error("Error creating custom place:", error)
      throw error
    }
  },

  // Update custom place
  async updateCustomPlace(placeId, placeData) {
    try {
      const response = await axios.put(`/api/v1/places/custom/${placeId}`, placeData)
      return response.data
    } catch (error) {
      console.error("Error updating custom place:", error)
      throw error
    }
  },

  // Delete custom place
  async deleteCustomPlace(placeId) {
    try {
      const response = await axios.delete(`/api/v1/places/custom/${placeId}`)
      return response.data
    } catch (error) {
      console.error("Error deleting custom place:", error)
      throw error
    }
  },
}
