import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import BookAppointment from '../views/BookAppointment.vue'
import MyAppointments from '../views/MyAppointments.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/book',
    name: 'BookAppointment',
    component: BookAppointment
  },
  {
    path: '/appointments',
    name: 'MyAppointments',
    component: MyAppointments
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
