// Google Maps configuration
export const GOOGLE_MAPS_CONFIG = {
  apiKey: import.meta.env.VUE_APP_GOOGLE_MAPS_API_KEY,
  libraries: ["places", "geometry"],
  version: "weekly",
  language: "ko",
  region: "KR",
}

// Function to load Google Maps with proper configuration
export function loadGoogleMaps() {
  return new Promise((resolve, reject) => {
    // Check if already loaded
    if (window.google && window.google.maps) {
      resolve(window.google.maps)
      return
    }

    // Check if script is already being loaded
    if (window.googleMapsLoading) {
      // Wait for the existing load to complete
      const checkLoaded = () => {
        if (window.google && window.google.maps) {
          resolve(window.google.maps)
        } else {
          setTimeout(checkLoaded, 100)
        }
      }
      checkLoaded()
      return
    }

    window.googleMapsLoading = true

    // Create callback function
    window.initGoogleMaps = () => {
      window.googleMapsLoading = false
      console.log("Google Maps loaded successfully")

      // Dispatch custom event
      window.dispatchEvent(new CustomEvent("google-maps-loaded"))

      resolve(window.google.maps)
    }

    // Check if API key exists
    if (!GOOGLE_MAPS_CONFIG.apiKey) {
      console.error("Google Maps API key is missing. Please check your .env file.")
      reject(new Error("Google Maps API key is missing"))
      return
    }

    // Create script element
    const script = document.createElement("script")
    script.src = `https://maps.googleapis.com/maps/api/js?key=${GOOGLE_MAPS_CONFIG.apiKey}&libraries=${GOOGLE_MAPS_CONFIG.libraries.join(",")}&callback=initGoogleMaps&v=${GOOGLE_MAPS_CONFIG.version}&language=${GOOGLE_MAPS_CONFIG.language}&region=${GOOGLE_MAPS_CONFIG.region}`
    script.async = true
    script.defer = true

    script.onerror = () => {
      window.googleMapsLoading = false
      console.error("Failed to load Google Maps API - check your API key and network connection")
      reject(new Error("Failed to load Google Maps API"))
    }

    document.head.appendChild(script)
  })
}

// Function to check if Distance Matrix API is available
export function checkDistanceMatrixAPI() {
  if (!window.google || !window.google.maps) {
    return false
  }

  try {
    new window.google.maps.DistanceMatrixService()
    return true
  } catch (error) {
    console.error("Distance Matrix API not available:", error)
    return false
  }
}
