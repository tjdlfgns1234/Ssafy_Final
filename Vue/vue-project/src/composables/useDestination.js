import { ref } from "vue"

// Create reactive state
const destinations = ref([])
const currentDestination = ref(null)
const isLoading = ref(false)
const error = ref(null)

// Create the composable
export function useDestination() {
  // Methods
  function fetchDestinations(region) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        // Mock destinations data based on region
        const mockDestinations = {
          seoul: [
            {
              id: 1,
              name: "Gyeongbokgung Palace",
              description: "The largest of the Five Grand Palaces built during the Joseon Dynasty.",
              image: "https://via.placeholder.com/400x300?text=Gyeongbokgung",
              location: "Seoul, South Korea",
              rating: 4.7,
              reviews: 1250,
              category: "Historical",
            },
            {
              id: 2,
              name: "Namsan Seoul Tower",
              description: "A communication and observation tower located on Namsan Mountain in central Seoul.",
              image: "https://via.placeholder.com/400x300?text=Namsan+Tower",
              location: "Seoul, South Korea",
              rating: 4.5,
              reviews: 980,
              category: "Landmark",
            },
            {
              id: 3,
              name: "Bukchon Hanok Village",
              description: "A Korean traditional village with a 600-year history.",
              image: "https://via.placeholder.com/400x300?text=Bukchon+Hanok",
              location: "Seoul, South Korea",
              rating: 4.6,
              reviews: 850,
              category: "Cultural",
            },
          ],
          busan: [
            {
              id: 4,
              name: "Haeundae Beach",
              description: "One of the most famous beaches in Korea.",
              image: "https://via.placeholder.com/400x300?text=Haeundae+Beach",
              location: "Busan, South Korea",
              rating: 4.8,
              reviews: 1500,
              category: "Nature",
            },
            {
              id: 5,
              name: "Gamcheon Culture Village",
              description:
                'Known as the "Machu Picchu of Busan" with its stairway streets and brightly painted houses.',
              image: "https://via.placeholder.com/400x300?text=Gamcheon+Village",
              location: "Busan, South Korea",
              rating: 4.7,
              reviews: 1100,
              category: "Cultural",
            },
          ],
          jeju: [
            {
              id: 6,
              name: "Seongsan Ilchulbong",
              description: "A tuff cone formed by hydrovolcanic eruptions.",
              image: "https://via.placeholder.com/400x300?text=Seongsan+Ilchulbong",
              location: "Jeju Island, South Korea",
              rating: 4.9,
              reviews: 2000,
              category: "Nature",
            },
            {
              id: 7,
              name: "Hallasan Mountain",
              description: "The highest mountain in South Korea and a shield volcano.",
              image: "https://via.placeholder.com/400x300?text=Hallasan",
              location: "Jeju Island, South Korea",
              rating: 4.8,
              reviews: 1800,
              category: "Nature",
            },
          ],
        }

        destinations.value = mockDestinations[region] || []
        isLoading.value = false
        resolve(destinations.value)
      }, 1000)
    })
  }

  function fetchDestinationById(id) {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        // Convert id to number for comparison
        const numId = Number.parseInt(id)

        // Mock destination data
        const allDestinations = [
          {
            id: 1,
            name: "Gyeongbokgung Palace",
            description: "The largest of the Five Grand Palaces built during the Joseon Dynasty.",
            fullDescription:
              'Gyeongbokgung Palace was the main royal palace of the Joseon dynasty. Built in 1395, it is located in northern Seoul and is the largest of the Five Grand Palaces built by the Joseon dynasty. The name means "Palace Greatly Blessed by Heaven".',
            image: "https://via.placeholder.com/800x600?text=Gyeongbokgung",
            images: [
              "https://via.placeholder.com/800x600?text=Gyeongbokgung+1",
              "https://via.placeholder.com/800x600?text=Gyeongbokgung+2",
              "https://via.placeholder.com/800x600?text=Gyeongbokgung+3",
            ],
            location: "Seoul, South Korea",
            coordinates: { lat: 37.5796, lng: 126.977 },
            rating: 4.7,
            reviews: 1250,
            category: "Historical",
            openingHours: "9:00 AM - 6:00 PM",
            admissionFee: "₩3,000",
            website: "http://www.royalpalace.go.kr/",
            facilities: ["Restrooms", "Gift Shop", "Guided Tours"],
            nearbyAttractions: [
              { id: 8, name: "Insadong", distance: "1.2 km" },
              { id: 9, name: "Bukchon Hanok Village", distance: "1.5 km" },
            ],
          },
          // Add more detailed destination data as needed
        ]

        currentDestination.value = allDestinations.find((dest) => dest.id === numId) || null
        isLoading.value = false
        resolve(currentDestination.value)
      }, 1000)
    })
  }

  // Return the state and methods
  return {
    // State
    destinations,
    currentDestination,
    isLoading,
    error,

    // Methods
    fetchDestinations,
    fetchDestinationById,
  }
}
