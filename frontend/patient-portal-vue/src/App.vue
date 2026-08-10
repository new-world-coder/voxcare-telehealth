<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">VoxCare</div>
      <div class="nav-links">
        <router-link to="/">Home</router-link>
        <router-link to="/appointments">Appointments</router-link>
        <router-link to="/providers">Providers</router-link>
        <router-link to="/profile">Profile</router-link>
        <button v-if="!isAuthenticated" @click="login">Login</button>
        <button v-else @click="logout">Logout</button>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useAuthStore } from './stores/auth'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    
    const login = () => {
      // Redirect to login page or show login modal
      console.log('Login clicked')
    }
    
    const logout = () => {
      authStore.logout()
    }
    
    return {
      isAuthenticated,
      login,
      logout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  background-color: #f5f5f5;
}

.navbar {
  background-color: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.nav-links a:hover {
  background-color: #34495e;
}

.nav-links button {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.nav-links button:hover {
  background-color: #2980b9;
}

.main-content {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
