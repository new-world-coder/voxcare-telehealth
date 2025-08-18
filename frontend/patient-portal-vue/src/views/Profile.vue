<template>
  <div class="profile">
    <h2>My Profile</h2>
    
    <div class="profile-content">
      <div class="profile-section">
        <h3>Personal Information</h3>
        <form @submit.prevent="updateProfile" class="profile-form">
          <div class="form-row">
            <div class="form-group">
              <label>First Name</label>
              <input type="text" v-model="profile.firstName" required>
            </div>
            <div class="form-group">
              <label>Last Name</label>
              <input type="text" v-model="profile.lastName" required>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Email</label>
              <input type="email" v-model="profile.email" required>
            </div>
            <div class="form-group">
              <label>Phone</label>
              <input type="tel" v-model="profile.phone">
            </div>
          </div>
          
          <div class="form-group">
            <label>Date of Birth</label>
            <input type="date" v-model="profile.dateOfBirth">
          </div>
          
          <div class="form-group">
            <label>Address</label>
            <textarea v-model="profile.address" rows="3"></textarea>
          </div>
          
          <button type="submit" class="btn btn-primary">Update Profile</button>
        </form>
      </div>
      
      <div class="profile-section">
        <h3>Medical Information</h3>
        <div class="medical-info">
          <div class="form-group">
            <label>Allergies</label>
            <textarea v-model="profile.allergies" rows="3" placeholder="List any allergies..."></textarea>
          </div>
          
          <div class="form-group">
            <label>Current Medications</label>
            <textarea v-model="profile.medications" rows="3" placeholder="List current medications..."></textarea>
          </div>
          
          <div class="form-group">
            <label>Medical History</label>
            <textarea v-model="profile.medicalHistory" rows="4" placeholder="Relevant medical history..."></textarea>
          </div>
          
          <button @click="saveMedicalInfo" class="btn btn-secondary">Save Medical Info</button>
        </div>
      </div>
      
      <div class="profile-section">
        <h3>Account Settings</h3>
        <div class="account-settings">
          <button @click="showChangePassword = true" class="btn btn-secondary">
            Change Password
          </button>
          <button @click="showDeleteAccount = true" class="btn btn-danger">
            Delete Account
          </button>
        </div>
      </div>
    </div>

    <!-- Change Password Modal -->
    <div v-if="showChangePassword" class="modal-overlay" @click="showChangePassword = false">
      <div class="modal" @click.stop>
        <h3>Change Password</h3>
        <form @submit.prevent="changePassword">
          <div class="form-group">
            <label>Current Password</label>
            <input type="password" v-model="passwordForm.currentPassword" required>
          </div>
          
          <div class="form-group">
            <label>New Password</label>
            <input type="password" v-model="passwordForm.newPassword" required>
          </div>
          
          <div class="form-group">
            <label>Confirm New Password</label>
            <input type="password" v-model="passwordForm.confirmPassword" required>
          </div>
          
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="showChangePassword = false">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">Change Password</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Account Modal -->
    <div v-if="showDeleteAccount" class="modal-overlay" @click="showDeleteAccount = false">
      <div class="modal" @click.stop>
        <h3>Delete Account</h3>
        <p class="warning">This action cannot be undone. All your data will be permanently deleted.</p>
        
        <div class="form-group">
          <label>Type "DELETE" to confirm</label>
          <input type="text" v-model="deleteConfirmation" placeholder="DELETE">
        </div>
        
        <div class="modal-actions">
          <button type="button" class="btn btn-secondary" @click="showDeleteAccount = false">
            Cancel
          </button>
          <button 
            type="button" 
            class="btn btn-danger" 
            @click="deleteAccount"
            :disabled="deleteConfirmation !== 'DELETE'"
          >
            Delete Account
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

export default {
  name: 'Profile',
  setup() {
    const authStore = useAuthStore()
    const showChangePassword = ref(false)
    const showDeleteAccount = ref(false)
    const deleteConfirmation = ref('')
    
    const profile = ref({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      dateOfBirth: '',
      address: '',
      allergies: '',
      medications: '',
      medicalHistory: ''
    })
    
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const loadProfile = async () => {
      try {
        const response = await axios.get('/api/patients/profile')
        profile.value = { ...profile.value, ...response.data }
      } catch (error) {
        console.error('Failed to load profile:', error)
        // Mock data for development
        profile.value = {
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          phone: '+1-555-0123',
          dateOfBirth: '1990-01-01',
          address: '123 Main St, Anytown, USA',
          allergies: 'None',
          medications: 'None',
          medicalHistory: 'No significant medical history'
        }
      }
    }

    const updateProfile = async () => {
      try {
        await axios.put('/api/patients/profile', profile.value)
        alert('Profile updated successfully!')
      } catch (error) {
        console.error('Failed to update profile:', error)
        alert('Failed to update profile')
      }
    }

    const saveMedicalInfo = async () => {
      try {
        await axios.put('/api/patients/medical-info', {
          allergies: profile.value.allergies,
          medications: profile.value.medications,
          medicalHistory: profile.value.medicalHistory
        })
        alert('Medical information saved successfully!')
      } catch (error) {
        console.error('Failed to save medical info:', error)
        alert('Failed to save medical information')
      }
    }

    const changePassword = async () => {
      if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        alert('New passwords do not match')
        return
      }
      
      try {
        await axios.post('/api/auth/change-password', {
          currentPassword: passwordForm.value.currentPassword,
          newPassword: passwordForm.value.newPassword
        })
        
        alert('Password changed successfully!')
        showChangePassword.value = false
        passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
      } catch (error) {
        console.error('Failed to change password:', error)
        alert('Failed to change password')
      }
    }

    const deleteAccount = async () => {
      if (deleteConfirmation.value !== 'DELETE') {
        alert('Please type DELETE to confirm')
        return
      }
      
      if (confirm('Are you absolutely sure you want to delete your account? This cannot be undone.')) {
        try {
          await axios.delete('/api/patients/profile')
          authStore.logout()
          // Redirect to home or login
        } catch (error) {
          console.error('Failed to delete account:', error)
          alert('Failed to delete account')
        }
      }
    }

    onMounted(() => {
      loadProfile()
    })

    return {
      profile,
      passwordForm,
      showChangePassword,
      showDeleteAccount,
      deleteConfirmation,
      updateProfile,
      saveMedicalInfo,
      changePassword,
      deleteAccount
    }
  }
}
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
}

.profile h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.profile-section {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-section h3 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 0.5rem;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  resize: vertical;
}

.medical-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.account-settings {
  display: flex;
  gap: 1rem;
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
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background-color: #c0392b;
}

.btn-danger:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal h3 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.warning {
  background-color: #fff3cd;
  color: #856404;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
  border: 1px solid #ffeaa7;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .account-settings {
    flex-direction: column;
  }
  
  .modal {
    margin: 1rem;
    padding: 1.5rem;
  }
}
</style>
