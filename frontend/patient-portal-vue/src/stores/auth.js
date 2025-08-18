import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)

  const isAuthenticated = computed(() => !!token.value)

  const login = async (email, password) => {
    try {
      const response = await axios.post('/api/auth/login', { email, password })
      const { token: newToken, refreshToken: newRefreshToken, user: userData } = response.data
      
      token.value = newToken
      refreshToken.value = newRefreshToken
      user.value = userData
      
      localStorage.setItem('token', newToken)
      localStorage.setItem('refreshToken', newRefreshToken)
      
      return { success: true }
    } catch (error) {
      console.error('Login failed:', error)
      return { success: false, error: error.response?.data?.message || 'Login failed' }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    refreshToken.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  const refreshAuth = async () => {
    if (!refreshToken.value) return false
    
    try {
      const response = await axios.post('/api/auth/refresh', { 
        refreshToken: refreshToken.value 
      })
      const { token: newToken } = response.data
      
      token.value = newToken
      localStorage.setItem('token', newToken)
      
      return true
    } catch (error) {
      console.error('Token refresh failed:', error)
      logout()
      return false
    }
  }

  return {
    user,
    token,
    refreshToken,
    isAuthenticated,
    login,
    logout,
    refreshAuth
  }
})
