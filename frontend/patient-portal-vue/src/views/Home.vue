<template>
  <div class="home">
    <div class="hero">
      <h1>Welcome to VoxCare</h1>
      <p>Your trusted telehealth platform for convenient healthcare</p>
      <div class="hero-buttons">
        <router-link to="/appointments" class="btn btn-primary">Book Appointment</router-link>
        <router-link to="/providers" class="btn btn-secondary">Find Providers</router-link>
      </div>
    </div>
    
    <div class="features">
      <div class="feature-card">
        <h3>Easy Scheduling</h3>
        <p>Book appointments with healthcare providers in just a few clicks</p>
      </div>
      <div class="feature-card">
        <h3>Secure Telehealth</h3>
        <p>Join video consultations from the comfort of your home</p>
      </div>
      <div class="feature-card">
        <h3>24/7 Access</h3>
        <p>Manage your healthcare anytime, anywhere</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  text-align: center;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 2rem;
  border-radius: 12px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s;
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
  background-color: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background-color: white;
  color: #667eea;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style>
