import axios from './axiosConfig'

const API_URL = import.meta.env.VITE_API_URL || 'http://192.168.205.81:8080'

export const travelPlanService = {
  // Get all travel plans for a user
  async getAllPlansByUser(userId) {
    try {
      const response = await axios.get(`${API_URL}/api/v1/travelplans/user/${userId}`)

      const data = response.data
      console.log('Travel plans fetched from server:', data)

      // Handle different response formats
      if (Array.isArray(data)) {
        return data
      } else if (data.data && Array.isArray(data.data)) {
        return data.data
      } else if (data.content && Array.isArray(data.content)) {
        return data.content
      } else if (data.result && Array.isArray(data.result)) {
        return data.result
      } else {
        console.warn('Unexpected response format:', data)
        return []
      }
    } catch (error) {
      console.error('Error fetching travel plans:', error)
      throw error
    }
  },

  // Save a new travel plan
  async savePlan(planData) {
    try {
      console.log('Saving travel plan:', planData)

      // Map to server DTO format exactly (no planId for new plans)
      const serverData = {
        planId: 0,
        userId: planData.user_id, // Use user_id from planData
        description: planData.description,
        routes:
          typeof planData.routes === 'string'
            ? planData.routes
            : JSON.stringify(planData.routes || planData.destinations || []),
        start_day: planData.start_day,
        end_day: planData.end_day,
      }

      console.log('Sending to server:', serverData)

      const response = await axios.post(`${API_URL}/api/v1/travelplans`, serverData)
      const data = response.data
      console.log('Travel plan saved to server:', data)
      return data
    } catch (error) {
      console.error('Error saving travel plan:', error)
      throw error
    }
  },

  // Update an existing travel plan
  async updatePlan(planId, planData) {
    try {
      console.log(`Updating travel plan: ${planId}`, planData)

      // Map to server DTO format exactly
      const serverData = {
        planId: planId,
        userId: planData.user_id, // Use user_id from planData
        description: planData.description,
        routes:
          typeof planData.routes === 'string'
            ? planData.routes
            : JSON.stringify(planData.routes || planData.destinations || []),
        start_day: planData.start_day,
        end_day: planData.end_day,
      }

      console.log('Sending to server:', serverData)

      const response = await axios.put(`${API_URL}/api/v1/travelplans/${planId}`, serverData)
      const data = response.data
      console.log('Travel plan updated on server:', data)
      return data
    } catch (error) {
      console.error('Error updating travel plan:', error)
      throw error
    }
  },

  // Delete a travel plan
  async deletePlan(planId) {
    try {
      console.log(`Deleting travel plan: ${planId}`)
      const response = await axios.delete(`${API_URL}/api/v1/travelplans/${planId}`)

      // DELETE might return empty response or success message
      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('application/json')) {
        const data = response.data
        console.log('Travel plan deleted from server:', data)
        return data
      } else {
        console.log('Travel plan deleted from server (no response body)')
        return { success: true }
      }
    } catch (error) {
      console.error('Error deleting travel plan:', error)
      throw error
    }
  },

  // Add destination to travel plan
  async addDestinationToPlan(planId, destinationData) {
    try {
      console.log(`Adding destination to plan: ${planId}`, destinationData)

      const serverData = {
        name: destinationData.name,
        day: destinationData.day,
        order: destinationData.order,
        duration: destinationData.duration,
        notes: destinationData.notes,
        image: destinationData.image,
        latitude: destinationData.latitude,
        longitude: destinationData.longitude,
        planId: planId,
      }

      const response = await axios.post(
        `${API_URL}/api/v1/travelplans/${planId}/destinations`,
        serverData,
      )
      const data = response.data
      console.log('Destination added to plan:', data)
      return data
    } catch (error) {
      console.error('Error adding destination to plan:', error)
      throw error
    }
  },

  // Remove destination from travel plan
  async removeDestinationFromPlan(planId, destinationId) {
    try {
      console.log(`Removing destination ${destinationId} from plan: ${planId}`)
      const response = await axios.delete(
        `${API_URL}/api/v1/travelplans/${planId}/destinations/${destinationId}`,
      )
      const data = response.data
      console.log('Destination removed from plan:', data)
      return data
    } catch (error) {
      console.error('Error removing destination from plan:', error)
      throw error
    }
  },

  // Update destination order in travel plan
  async updateDestinationOrder(planId, destinationId, orderData) {
    try {
      console.log(`Updating destination order in plan: ${planId}`, orderData)

      const serverData = {
        day: orderData.day,
        order: orderData.order,
        planId: planId,
        destination_id: orderData.destination_id,
      }

      const response = await axios.put(
        `${API_URL}/api/v1/travelplans/${planId}/destinations/${destinationId}/order`,
        serverData,
      )
      const data = response.data
      console.log('Destination order updated:', data)
      return data
    } catch (error) {
      console.error('Error updating destination order:', error)
      throw error
    }
  },

  // Get real-time travel data for a plan
  async getFirstDestinationRealTime(planId) {
    try {
      console.log(`Getting real-time data for plan: ${planId}`)
      const response = await axios.get(`${API_URL}/api/v1/travelplans/${planId}/realtime`)
      const data = response.data
      console.log('Real-time travel data fetched:', data)
      return data
    } catch (error) {
      console.error('Error fetching real-time travel data:', error)
      throw error
    }
  },

  // Update travel times with real-time data
  async updateTravelTimes(planId, travelTimeData) {
    try {
      console.log(`Updating travel times for plan: ${planId}`, travelTimeData)

      const serverData = {
        planId: planId,
        travelTimes: Array.isArray(travelTimeData) ? travelTimeData : [travelTimeData],
      }

      const response = await axios.put(
        `${API_URL}/api/v1/travelplans/${planId}/travel-times`,
        serverData,
      )
      const data = response.data
      console.log('Travel times updated:', data)
      return data
    } catch (error) {
      console.error('Error updating travel times:', error)
      throw error
    }
  },
}
