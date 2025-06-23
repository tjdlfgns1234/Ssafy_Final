/**
 * Convert client data to server DTO format for TravelPlan
 * @param {Object} planData - Client-side travel plan data
 * @param {Object} authUser - Authenticated user object
 * @param {number|null} planId - Plan ID (null for new plans)
 * @returns {Object} - Data object matching server TravelPlanDTO format
 */
export function convertToTravelPlanDTO(planData, authUser, planId = null) {
  return {
    title: planData.title,
    planId: planId,
    userId: authUser?.mno || authUser?.id, // Use mno from auth user
    description: planData.description,
    routes: planData.routes || JSON.stringify(planData.destinations || []),
    start_day: planData.startDate,
    end_day: planData.endDate,
  }
}

/**
 * Convert snake_case field names to camelCase field names for other services
 * @param {Object} data - Data object with snake_case field names
 * @returns {Object} - Data object with camelCase field names
 */
export function convertToServerFields(data) {
  const converted = { ...data }

  // Convert user_id to userId (for other services)
  if (converted.user_id !== undefined) {
    converted.userId = converted.user_id
    delete converted.user_id
  }

  // Convert plan_id to planId (for other services)
  if (converted.plan_id !== undefined) {
    converted.planId = converted.plan_id
    delete converted.plan_id
  }

  // Convert place_id to placeId (for other services)
  if (converted.place_id !== undefined) {
    converted.placeId = converted.place_id
    delete converted.place_id
  }

  return converted
}

/**
 * Convert camelCase field names to snake_case field names from server response
 * @param {Object} data - Data object with camelCase field names
 * @returns {Object} - Data object with snake_case field names
 */
export function convertFromServerFields(data) {
  const converted = { ...data }

  // Convert userId to user_id
  if (converted.userId !== undefined) {
    converted.user_id = converted.userId
    delete converted.userId
  }

  // Convert planId to plan_id
  if (converted.planId !== undefined) {
    converted.plan_id = converted.planId
    delete converted.planId
  }

  // Convert placeId to place_id
  if (converted.placeId !== undefined) {
    converted.place_id = converted.placeId
    delete converted.placeId
  }

  return converted
}
