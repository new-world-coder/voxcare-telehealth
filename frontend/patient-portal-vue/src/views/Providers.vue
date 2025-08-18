<template>
  <div class="providers">
    <h2>Healthcare Providers</h2>
    
    <div class="providers-grid">
      <div 
        v-for="provider in providers" 
        :key="provider.id" 
        class="provider-card"
      >
        <div class="provider-avatar">
          <img :src="provider.avatar || '/default-avatar.png'" :alt="provider.name">
        </div>
        
        <div class="provider-info">
          <h3>{{ provider.name }}</h3>
          <p class="specialty">{{ provider.specialty }}</p>
          <p class="description">{{ provider.description }}</p>
          
          <div class="provider-stats">
            <span class="stat">
              <strong>{{ provider.experience }}</strong> years experience
            </span>
            <span class="stat">
              <strong>{{ provider.rating }}</strong> ⭐ rating
            </span>
          </div>
          
          <div class="provider-actions">
            <button class="btn btn-primary" @click="bookWithProvider(provider.id)">
              Book Appointment
            </button>
            <button class="btn btn-secondary" @click="viewProfile(provider.id)">
              View Profile
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'Providers',
  setup() {
    const providers = ref([])
    const router = useRouter()

    const loadProviders = async () => {
      try {
        const response = await axios.get('/api/providers')
        providers.value = response.data
      } catch (error) {
        console.error('Failed to load providers:', error)
        // Mock data for development
        providers.value = [
          {
            id: 1,
            name: 'Dr. Sarah Johnson',
            specialty: 'Cardiology',
            description: 'Board-certified cardiologist with expertise in preventive cardiology and heart failure management.',
            experience: 15,
            rating: 4.8,
            avatar: null
          },
          {
            id: 2,
            name: 'Dr. Michael Chen',
            specialty: 'Dermatology',
            description: 'Specialized in medical and cosmetic dermatology, treating all skin conditions.',
            experience: 12,
            rating: 4.9,
            avatar: null
          },
          {
            id: 3,
            name: 'Dr. Emily Rodriguez',
            specialty: 'Pediatrics',
            description: 'Caring pediatrician focused on child development and preventive care.',
            experience: 8,
            rating: 4.7,
            avatar: null
          }
        ]
      }
    }

    const bookWithProvider = (providerId) => {
      router.push({ 
        path: '/appointments', 
        query: { provider: providerId } 
      })
    }

    const viewProfile = (providerId) => {
      // Implementation for viewing detailed provider profile
      console.log('View profile:', providerId)
    }

    onMounted(() => {
      loadProviders()
    })

    return {
      providers,
      bookWithProvider,
      viewProfile
    }
  }
}
</script>

<style scoped>
.providers {
  max-width: 1200px;
  margin: 0 auto;
}

.providers h2 {
  text-align: center;
  margin-bottom: 3rem;
  color: #2c3e50;
}

.providers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
}

.provider-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.provider-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.provider-avatar {
  text-align: center;
  margin-bottom: 1.5rem;
}

.provider-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #3498db;
}

.provider-info h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 1.5rem;
}

.specialty {
  color: #3498db;
  font-weight: 600;
  margin-bottom: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.description {
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.provider-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.stat {
  background-color: #f8f9fa;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  color: #2c3e50;
}

.provider-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
  flex: 1;
  text-align: center;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

@media (max-width: 768px) {
  .providers-grid {
    grid-template-columns: 1fr;
  }
  
  .provider-card {
    padding: 1.5rem;
  }
  
  .provider-stats {
    flex-direction: column;
  }
  
  .provider-actions {
    flex-direction: column;
  }
}
</style>
