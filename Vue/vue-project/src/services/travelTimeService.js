// Travel time calculation service
export class TravelTimeService {
  constructor() {
    this.googleMapsLoaded = false
    this.initGoogleMaps()
  }

  initGoogleMaps() {
    // Check if Google Maps is already loaded
    if (window.google && window.google.maps) {
      this.googleMapsLoaded = true
      return
    }

    // Wait for Google Maps to load
    const checkGoogleMaps = () => {
      if (window.google && window.google.maps) {
        this.googleMapsLoaded = true
      } else {
        setTimeout(checkGoogleMaps, 100)
      }
    }
    checkGoogleMaps()
  }

  async calculateRealTravelTime(fromLat, fromLng, toLat, toLng, mode = 'DRIVING') {
    if (!this.googleMapsLoaded || !window.google || !window.google.maps) {
      console.warn('Google Maps not loaded yet')
      return { error: 'Google Maps not loaded', duration: null }
    }

    // Validate coordinates
    if (
      isNaN(Number.parseFloat(fromLat)) ||
      isNaN(Number.parseFloat(fromLng)) ||
      isNaN(Number.parseFloat(toLat)) ||
      isNaN(Number.parseFloat(toLng))
    ) {
      console.error('Invalid coordinates provided')
      return { error: 'Invalid coordinates', duration: null }
    }

    // Check if start and end coordinates are the same (within a small tolerance)
    const latDiff = Math.abs(Number.parseFloat(fromLat) - Number.parseFloat(toLat))
    const lngDiff = Math.abs(Number.parseFloat(fromLng) - Number.parseFloat(toLng))
    const tolerance = 0.0001 // About 10 meters

    if (latDiff < tolerance && lngDiff < tolerance) {
      console.log(`⏭️ Same location detected, returning 0 travel time`)
      return {
        duration: 0, // 0 seconds
        durationText: '0분',
        distance: 0, // 0 meters
        distanceText: '0m',
        status: 'OK',
        error: null,
      }
    }

    return new Promise((resolve) => {
      const service = new window.google.maps.DistanceMatrixService()
      service.getDistanceMatrix(
        {
          origins: [{ lat: Number.parseFloat(fromLat), lng: Number.parseFloat(fromLng) }],
          destinations: [{ lat: Number.parseFloat(toLat), lng: Number.parseFloat(toLng) }],
          travelMode: window.google.maps.TravelMode[mode],
          unitSystem: window.google.maps.UnitSystem.METRIC,
          avoidHighways: false,
          avoidTolls: false,
        },
        (response, status) => {
          if (status === 'OK') {
            const element = response.rows[0].elements[0]
            if (element.status === 'OK') {
              const duration = element.duration
              const distance = element.distance

              resolve({
                duration: duration.value, // in seconds
                durationText: duration.text,
                distance: distance.value, // in meters
                distanceText: distance.text,
                status: 'OK',
                error: null,
              })
            } else {
              console.warn('Distance calculation failed:', element.status)
              resolve({
                error: `Distance calculation failed: ${element.status}`,
                duration: null,
                status: element.status,
              })
            }
          } else {
            console.error('Distance Matrix service failed:', status)
            resolve({
              error: `Distance Matrix service failed: ${status}`,
              duration: null,
              status: status,
            })
          }
        },
      )
    })
  }

  // Calculate travel times for a complete route
  async calculateRouteSegments(destinations, startLocation = null, endLocation = null) {
    const segments = []
    const allPoints = []

    // Add start location if provided
    if (startLocation) {
      allPoints.push({
        name: '출발 지점',
        lat: startLocation.lat,
        lng: startLocation.lng,
        isStart: true,
      })
    }

    // Add destinations
    destinations.forEach((dest) => {
      allPoints.push({
        name: dest.name,
        lat: dest.latitude || dest.lat,
        lng: dest.longitude || dest.lng,
        id: dest.id,
        duration: dest.duration,
      })
    })

    // Add end location if provided and different from start
    if (
      endLocation &&
      (endLocation.lat !== startLocation?.lat || endLocation.lng !== startLocation?.lng)
    ) {
      allPoints.push({
        name: '도착 지점',
        lat: endLocation.lat,
        lng: endLocation.lng,
        isEnd: true,
      })
    }

    // Calculate travel times between consecutive points
    for (let i = 0; i < allPoints.length - 1; i++) {
      const from = allPoints[i]
      const to = allPoints[i + 1]

      try {
        const travelTime = await this.calculateRealTravelTime(
          from.lat,
          from.lng,
          to.lat,
          to.lng,
          'TRANSIT',
        )

        segments.push({
          from: from.name,
          to: to.name,
          fromId: from.id,
          toId: to.id,
          travelTime: travelTime,
          isStartSegment: from.isStart,
          isEndSegment: to.isEnd,
        })

        // Add delay between requests to avoid rate limiting
        await new Promise((resolve) => setTimeout(resolve, 200))
      } catch (error) {
        console.error(`Error calculating travel time from ${from.name} to ${to.name}:`, error)
        segments.push({
          from: from.name,
          to: to.name,
          fromId: from.id,
          toId: to.id,
          travelTime: null,
          error: error.message,
        })
      }
    }

    return segments
  }

  // Format duration in Korean with error handling
  formatDurationKorean(durationInSeconds) {
    if (!durationInSeconds || durationInSeconds === null) return 'N/A'

    const hours = Math.floor(durationInSeconds / 3600)
    const minutes = Math.floor((durationInSeconds % 3600) / 60)

    if (hours > 0) {
      if (minutes > 0) {
        return `${hours}시간 ${minutes}분`
      } else {
        return `${hours}시간`
      }
    } else {
      return `${minutes}분`
    }
  }

  // Calculate total travel time for a day
  calculateTotalTravelTime(segments) {
    if (!segments || segments.length === 0) return null

    let totalDuration = 0
    let totalDistance = 0

    segments.forEach((segment) => {
      if (segment.travelTime && segment.travelTime.duration) {
        totalDuration += segment.travelTime.duration

        if (segment.travelTime.distance) {
          totalDistance += segment.travelTime.distance
        }
      }
    })

    return {
      duration: totalDuration,
      distance: totalDistance,
      text: this.formatDurationKorean(totalDuration),
    }
  }

  formatDurationKorean(seconds) {
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

export default new TravelTimeService()
