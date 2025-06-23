import { defineStore } from "pinia"

export const useTravelPlanStore = defineStore("travelPlan", {
  state: () => ({
    travelPlans: [],
    currentPlan: null,
    isLoading: false,
    error: null,
  }),

  actions: {
    fetchTravelPlans() {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          this.travelPlans = [
            {
              id: 1,
              title: "Seoul Weekend Getaway",
              description: "A quick weekend trip to explore Seoul",
              startDate: "2023-11-10",
              endDate: "2023-11-12",
              destinations: [
                { id: 1, name: "Gyeongbokgung Palace", day: 1, order: 1 },
                { id: 2, name: "Namsan Seoul Tower", day: 1, order: 2 },
                { id: 3, name: "Bukchon Hanok Village", day: 2, order: 1 },
              ],
              image: "https://via.placeholder.com/400x300?text=Seoul+Trip",
              // Location data
              start_location_title: "서울역",
              place_id: "ChIJzWXFYYuifDUR64Pq5LTtioU",
              latitude_start: 37.5546,
              longitude_start: 126.9706,
              end_location_title: "서울역",
              latitude_end: 37.5546,
              longitude_end: 126.9706,
            },
            {
              id: 2,
              title: "Jeju Island Adventure",
              description: "Exploring the natural wonders of Jeju Island",
              startDate: "2023-12-15",
              endDate: "2023-12-20",
              destinations: [
                { id: 6, name: "Seongsan Ilchulbong", day: 1, order: 1 },
                { id: 7, name: "Hallasan Mountain", day: 2, order: 1 },
              ],
              image: "https://via.placeholder.com/400x300?text=Jeju+Trip",
              // Location data
              start_location_title: "제주국제공항",
              place_id: "ChIJRUDiP8i0cDURVL8_PJYpUhA",
              latitude_start: 33.5067,
              longitude_start: 126.493,
              end_location_title: "제주국제공항",
              latitude_end: 33.5067,
              longitude_end: 126.493,
            },
          ]
          this.isLoading = false
          resolve(this.travelPlans)
        }, 1000)
      })
    },

    fetchTravelPlanById(id) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          // Convert id to number for comparison
          const numId = Number.parseInt(id)

          const plan = this.travelPlans.find((plan) => plan.id === numId) || {
            id: numId,
            title: "Seoul Weekend Getaway",
            description: "A quick weekend trip to explore Seoul",
            startDate: "2023-11-10",
            endDate: "2023-11-12",
            // Location data
            start_location_title: "서울역",
            place_id: "ChIJzWXFYYuifDUR64Pq5LTtioU",
            latitude_start: 37.5546,
            longitude_start: 126.9706,
            end_location_title: "서울역",
            latitude_end: 37.5546,
            longitude_end: 126.9706,
            destinations: [
              {
                id: 1,
                name: "Gyeongbokgung Palace",
                day: 1,
                order: 1,
                duration: 120, // minutes
                notes: "Visit in the morning to avoid crowds",
                image: "https://via.placeholder.com/200x150?text=Gyeongbokgung",
              },
              {
                id: 2,
                name: "Namsan Seoul Tower",
                day: 1,
                order: 2,
                duration: 180, // minutes
                notes: "Great for sunset views",
                image: "https://via.placeholder.com/200x150?text=Namsan+Tower",
              },
              {
                id: 3,
                name: "Bukchon Hanok Village",
                day: 2,
                order: 1,
                duration: 240, // minutes
                notes: "Wear comfortable shoes for walking",
                image: "https://via.placeholder.com/200x150?text=Bukchon+Hanok",
              },
            ],
            routes: [
              {
                id: 1,
                from: 1, // Gyeongbokgung Palace
                to: 2, // Namsan Seoul Tower
                transportMode: "subway",
                duration: 30, // minutes
                instructions: "Take Line 3 from Gyeongbokgung Station to Myeongdong Station, then transfer to Line 4",
              },
              {
                id: 2,
                from: 2, // Namsan Seoul Tower
                to: 3, // Bukchon Hanok Village
                transportMode: "bus",
                duration: 45, // minutes
                instructions: "Take Bus 402 from Namsan stop to Bukchon stop",
              },
            ],
            image: "https://via.placeholder.com/400x300?text=Seoul+Trip",
          }

          this.currentPlan = plan
          this.isLoading = false
          resolve(this.currentPlan)
        }, 1000)
      })
    },

    createTravelPlan(planData) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          const newPlan = {
            id: Date.now(), // Generate a unique ID
            ...planData,
            destinations: [],
            routes: [],
            // Ensure location fields are included
            start_location_title: planData.start_location_title || "",
            place_id: planData.place_id || null,
            latitude_start: planData.latitude_start || null,
            longitude_start: planData.longitude_start || null,
            end_location_title: planData.end_location_title || "",
            latitude_end: planData.latitude_end || null,
            longitude_end: planData.longitude_end || null,
          }

          this.travelPlans.push(newPlan)
          this.currentPlan = newPlan
          this.isLoading = false
          resolve(newPlan)
        }, 1000)
      })
    },

    updateTravelPlan(planId, planData) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          const index = this.travelPlans.findIndex((plan) => plan.id === planId)

          if (index !== -1) {
            this.travelPlans[index] = {
              ...this.travelPlans[index],
              ...planData,
              // Ensure location fields are updated
              start_location_title: planData.start_location_title || this.travelPlans[index].start_location_title || "",
              place_id: planData.place_id || this.travelPlans[index].place_id || null,
              latitude_start: planData.latitude_start || this.travelPlans[index].latitude_start || null,
              longitude_start: planData.longitude_start || this.travelPlans[index].longitude_start || null,
              end_location_title: planData.end_location_title || this.travelPlans[index].end_location_title || "",
              latitude_end: planData.latitude_end || this.travelPlans[index].latitude_end || null,
              longitude_end: planData.longitude_end || this.travelPlans[index].longitude_end || null,
            }

            this.currentPlan = this.travelPlans[index]
          }

          this.isLoading = false
          resolve(this.currentPlan)
        }, 1000)
      })
    },

    deleteTravelPlan(planId) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          this.travelPlans = this.travelPlans.filter((plan) => plan.id !== planId)

          if (this.currentPlan && this.currentPlan.id === planId) {
            this.currentPlan = null
          }

          this.isLoading = false
          resolve(true)
        }, 1000)
      })
    },

    addDestinationToPlan(planId, destination) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          const plan = this.travelPlans.find((p) => p.id === planId)

          if (plan) {
            // Determine the next order for the day
            const destinationsForDay = plan.destinations.filter((d) => d.day === destination.day)
            const nextOrder =
              destinationsForDay.length > 0 ? Math.max(...destinationsForDay.map((d) => d.order)) + 1 : 1

            const newDestination = {
              ...destination,
              order: nextOrder,
            }

            plan.destinations.push(newDestination)

            if (this.currentPlan && this.currentPlan.id === planId) {
              this.currentPlan = { ...plan }
            }
          }

          this.isLoading = false
          resolve(plan)
        }, 1000)
      })
    },

    removeDestinationFromPlan(planId, destinationId) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          const plan = this.travelPlans.find((p) => p.id === planId)

          if (plan) {
            plan.destinations = plan.destinations.filter((d) => d.id !== destinationId)

            if (this.currentPlan && this.currentPlan.id === planId) {
              this.currentPlan = { ...plan }
            }
          }

          this.isLoading = false
          resolve(plan)
        }, 1000)
      })
    },

    reorderDestinations(planId, day, newOrder) {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          const plan = this.travelPlans.find((p) => p.id === planId)

          if (plan) {
            // Filter destinations for the specified day
            const dayDestinations = plan.destinations.filter((d) => d.day === day)
            const otherDestinations = plan.destinations.filter((d) => d.day !== day)

            // Update the order based on the new order array
            const updatedDayDestinations = newOrder.map((destId, index) => {
              const dest = dayDestinations.find((d) => d.id === destId)
              return { ...dest, order: index + 1 }
            })

            // Combine the updated day destinations with the other destinations
            plan.destinations = [...updatedDayDestinations, ...otherDestinations]

            if (this.currentPlan && this.currentPlan.id === planId) {
              this.currentPlan = { ...plan }
            }
          }

          this.isLoading = false
          resolve(plan)
        }, 1000)
      })
    },
  },
})
