import { ref } from "vue"
import { travelTimeService } from "../services/travelTimeService.js"

// Create reactive state
const travelPlans = ref([])
const currentPlan = ref(null)
const isLoading = ref(false)
const error = ref(null)

const travelTimes = ref({})
const isCalculatingTravelTimes = ref(false)

// Create the composable
export function useTravelPlan() {
  // Methods
  function fetchTravelPlans() {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        travelPlans.value = [
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
        isLoading.value = false
        resolve(travelPlans.value)
      }, 1000)
    })
  }

  function fetchTravelPlanById(id) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        // Convert id to number for comparison
        const numId = Number.parseInt(id)

        const plan = travelPlans.value.find((plan) => plan.id === numId) || {
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

        currentPlan.value = plan
        isLoading.value = false
        resolve(currentPlan.value)
      }, 1000)
    })
  }

  function createTravelPlan(planData) {
    isLoading.value = true
    error.value = null

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

        travelPlans.value.push(newPlan)
        currentPlan.value = newPlan
        isLoading.value = false
        resolve(newPlan)
      }, 1000)
    })
  }

  function updateTravelPlan(planId, planData) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        const index = travelPlans.value.findIndex((plan) => plan.id === planId)

        if (index !== -1) {
          travelPlans.value[index] = {
            ...travelPlans.value[index],
            ...planData,
            // Ensure location fields are updated
            start_location_title: planData.start_location_title || travelPlans.value[index].start_location_title || "",
            place_id: planData.place_id || travelPlans.value[index].place_id || null,
            latitude_start: planData.latitude_start || travelPlans.value[index].latitude_start || null,
            longitude_start: planData.longitude_start || travelPlans.value[index].longitude_start || null,
            end_location_title: planData.end_location_title || travelPlans.value[index].end_location_title || "",
            latitude_end: planData.latitude_end || travelPlans.value[index].latitude_end || null,
            longitude_end: planData.longitude_end || travelPlans.value[index].longitude_end || null,
          }

          currentPlan.value = travelPlans.value[index]
        }

        isLoading.value = false
        resolve(currentPlan.value)
      }, 1000)
    })
  }

  function deleteTravelPlan(planId) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        travelPlans.value = travelPlans.value.filter((plan) => plan.id !== planId)

        if (currentPlan.value && currentPlan.value.id === planId) {
          currentPlan.value = null
        }

        isLoading.value = false
        resolve(true)
      }, 1000)
    })
  }

  function addDestinationToPlan(planId, destination) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        const plan = travelPlans.value.find((p) => p.id === planId)

        if (plan) {
          // Determine the next order for the day
          const destinationsForDay = plan.destinations.filter((d) => d.day === destination.day)
          const nextOrder = destinationsForDay.length > 0 ? Math.max(...destinationsForDay.map((d) => d.order)) + 1 : 1

          const newDestination = {
            ...destination,
            order: nextOrder,
          }

          plan.destinations.push(newDestination)

          if (currentPlan.value && currentPlan.value.id === planId) {
            currentPlan.value = { ...plan }
          }
        }

        isLoading.value = false
        resolve(plan)
      }, 1000)
    })
  }

  function removeDestinationFromPlan(planId, destinationId) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        const plan = travelPlans.value.find((p) => p.id === planId)

        if (plan) {
          plan.destinations = plan.destinations.filter((d) => d.id !== destinationId)

          if (currentPlan.value && currentPlan.value.id === planId) {
            currentPlan.value = { ...plan }
          }
        }

        isLoading.value = false
        resolve(plan)
      }, 1000)
    })
  }

  function reorderDestinations(planId, day, newOrder) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        const plan = travelPlans.value.find((p) => p.id === planId)

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

          if (currentPlan.value && currentPlan.value.id === planId) {
            currentPlan.value = { ...plan }
          }
        }

        isLoading.value = false
        resolve(plan)
      }, 1000)
    })
  }

  function extractRoutesData(plan) {
    if (!plan) return null

    // Group destinations by day
    const dayDestinations = {}

    plan.destinations.forEach((dest) => {
      if (!dayDestinations[dest.day]) {
        dayDestinations[dest.day] = []
      }
      dayDestinations[dest.day].push(dest)
    })

    // Create dayLocations array
    const dayLocations = Object.entries(dayDestinations).map(([day, destinations]) => {
      // Sort destinations by order
      const sortedDestinations = [...destinations].sort((a, b) => a.order - b.order)

      // Map to the format expected by the API
      return {
        day: Number.parseInt(day),
        locations: sortedDestinations.map((dest) => ({
          id: dest.id,
          name: dest.name,
          latitude: dest.coordinates?.lat || 0,
          longitude: dest.coordinates?.lng || 0,
          duration: dest.duration || 0,
        })),
      }
    })

    // Create the final request object
    return {
      planId: plan.id,
      startLocation:
        plan.latitude_start && plan.longitude_start
          ? {
              name: plan.start_location_title || "Start",
              latitude: plan.latitude_start,
              longitude: plan.longitude_start,
            }
          : null,
      endLocation:
        plan.latitude_end && plan.longitude_end
          ? {
              name: plan.end_location_title || "End",
              latitude: plan.latitude_end,
              longitude: plan.longitude_end,
            }
          : null,
      dayLocations,
    }
  }

  async function applyOptimizationResult(planId, result) {
    if (!result.dayRoutes) {
      throw new Error("Invalid optimization result format")
    }

    // Process each day's route
    for (const dayRoute of result.dayRoutes) {
      const day = dayRoute.day
      const visits = dayRoute.visits

      if (!visits || visits.length === 0) continue

      // Create a new order array based on the visits
      const newOrder = visits.map((visit) => visit.locationId)

      // Update the order in the store
      await reorderDestinations(planId, day, newOrder)
    }

    // Recalculate travel times after optimization
    await calculateTravelTimesForPlan(planId)

    return true
  }

  async function optimizeRoute(planId) {
    isLoading.value = true
    error.value = null

    try {
      const plan = currentPlan.value || travelPlans.value.find((p) => p.id === planId)
      if (!plan) {
        throw new Error("Plan not found")
      }

      // Extract routes data
      const routesData = extractRoutesData(plan)

      // Call the recommendation service
      const recommendationService = await import("../services/recommendationService").then((m) => m.default)
      const result = await recommendationService.optimizeRoute(routesData)

      // Apply the optimization result
      await applyOptimizationResult(planId, result)

      return result
    } catch (err) {
      error.value = err.message || "Failed to optimize route"
      console.error("Route optimization error:", err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function calculateTravelTimesForPlan(planId) {
    isCalculatingTravelTimes.value = true

    try {
      const plan = currentPlan.value || travelPlans.value.find((p) => p.id === planId)
      if (!plan) return

      // Group destinations by day
      const destinationsByDay = {}
      plan.destinations.forEach((dest) => {
        if (!destinationsByDay[dest.day]) {
          destinationsByDay[dest.day] = []
        }
        destinationsByDay[dest.day].push(dest)
      })

      // Calculate travel times for each day
      const dayTravelTimes = {}

      for (const [day, destinations] of Object.entries(destinationsByDay)) {
        // Sort destinations by order
        const sortedDestinations = destinations.sort((a, b) => a.order - b.order)

        // Define start and end locations (can be customized)
        const startLocation =
          plan.latitude_start && plan.longitude_start
            ? {
                lat: plan.latitude_start,
                lng: plan.longitude_start,
                name: plan.start_location_title || "출발 지점",
              }
            : null

        const endLocation =
          plan.latitude_end && plan.longitude_end
            ? {
                lat: plan.latitude_end,
                lng: plan.longitude_end,
                name: plan.end_location_title || "도착 지점",
              }
            : startLocation // Use start as end if no end location

        // Calculate route segments
        const segments = await travelTimeService.calculateRouteSegments(sortedDestinations, startLocation, endLocation)

        // Calculate total travel time for the day
        const totalTime = travelTimeService.calculateTotalTravelTime(segments)

        dayTravelTimes[day] = {
          segments,
          totalTime,
          destinations: sortedDestinations,
        }
      }

      travelTimes.value[planId] = dayTravelTimes

      console.log("Travel times calculated:", dayTravelTimes)
    } catch (error) {
      console.error("Error calculating travel times:", error)
    } finally {
      isCalculatingTravelTimes.value = false
    }
  }

  function getTravelTimeForSegment(planId, day, fromId, toId) {
    const planTravelTimes = travelTimes.value[planId]
    if (!planTravelTimes || !planTravelTimes[day]) return null

    const segment = planTravelTimes[day].segments.find((s) => s.fromId === fromId && s.toId === toId)

    return segment?.travelTime
  }

  function getTotalTravelTimeForDay(planId, day) {
    const planTravelTimes = travelTimes.value[planId]
    if (!planTravelTimes || !planTravelTimes[day]) return null

    return planTravelTimes[day].totalTime
  }

  function formatTravelTime(travelTimeData) {
    if (!travelTimeData) return "N/A"
    if (travelTimeData.error) return "N/A"
    if (!travelTimeData.duration) return "N/A"

    return travelTimeService.formatDurationKorean(travelTimeData.duration)
  }

  // Return the state and methods
  return {
    // State
    travelPlans,
    currentPlan,
    isLoading,
    error,

    // New travel time state
    travelTimes,
    isCalculatingTravelTimes,

    // Methods
    fetchTravelPlans,
    fetchTravelPlanById,
    createTravelPlan,
    updateTravelPlan,
    deleteTravelPlan,
    addDestinationToPlan,
    removeDestinationFromPlan,
    reorderDestinations,

    // New travel time methods
    calculateTravelTimesForPlan,
    getTravelTimeForSegment,
    getTotalTravelTimeForDay,
    formatTravelTime,

    // 새로운 함수들
    extractRoutesData,
    applyOptimizationResult,
    optimizeRoute,
  }
}
