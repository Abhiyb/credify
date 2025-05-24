<template>
  <div class="min-h-screen bg-gray-100 font-sans">
    <!-- Hero Section -->
    <div class="relative bg-gradient-to-r from-indigo-600 to-purple-600 h-32 md:h-40 overflow-visible">
      <div class="w-full h-full bg-gradient-to-r from-indigo-600 to-purple-600 opacity-80"></div>
    </div>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 pb-12 relative z-20">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Left Sidebar -->
        <div class="space-y-8">
          <!-- Profile Picture & Basic Info -->
          <div class="bg-white rounded-xl shadow-lg p-8 text-center">
            <div class="mx-auto w-32 h-32 rounded-full border-4 border-white shadow-md mb-4 relative">
              <div class="w-full h-full flex items-center justify-center bg-gray-200 rounded-full text-3xl font-bold text-gray-500">
                {{ profile.fullName ? profile.fullName.charAt(0).toUpperCase() : '?' }}
              </div>
            </div>
            <h2 class="text-2xl font-bold text-gray-800">{{ profile.fullName }}</h2>
            <div class="mt-4">
              <span
                v-if="profile.isEligibleForBNPL"
                class="bg-green-100 text-green-800 px-3 py-1 rounded-full font-semibold"
              >
                ✅ Eligible for BNPL
              </span>
              <span
                v-else
                class="bg-red-100 text-red-800 px-3 py-1 rounded-full font-semibold"
              >
                ❌ Not eligible for BNPL
              </span>
            </div>
          </div>
          
          <!-- Profile Completion -->
          <div class="bg-white rounded-xl shadow-lg p-8">
            <h3 class="text-lg font-semibold text-gray-700 mb-4">Profile Completion</h3>
            <div class="w-full bg-gray-200 rounded-full h-2.5">
              <div
                :style="{ width: completionPercentage + '%' }"
                class="bg-indigo-600 h-2.5 rounded-full transition-all duration-500"
              ></div>
            </div>
            <p class="mt-2 text-sm text-gray-600">{{ completionPercentage }}% Complete</p>
          </div>
        </div>

        <!-- Right Content Area -->
        <div class="md:col-span-2 space-y-8">
          <!-- Loading State -->
          <div v-if="loading" class="flex justify-center items-center py-20">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
            <span class="ml-3 text-gray-600">Loading profile...</span>
          </div>

          <!-- Editable Profile Details -->
          <div v-else class="bg-white rounded-xl shadow-lg p-8">
            <div class="flex justify-between items-center mb-6 border-b pb-4">
              <h3 class="text-xl font-semibold text-gray-700">Personal Information</h3>
              <button
                v-if="!isEditing"
                @click="startEditing"
                class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition-colors"
              >
                Edit Profile
              </button>
            </div>
            
            <!-- View Mode -->
            <div v-if="!isEditing" class="space-y-4">
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"/>
                </svg>
                <span class="font-semibold">Full Name:</span>
                <span class="ml-2">{{ profile.fullName }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                  <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                </svg>
                <span class="font-semibold">Email:</span>
                <span class="ml-2">{{ profile.email }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                </svg>
                <span class="font-semibold">Phone:</span>
                <span class="ml-2">{{ profile.phone }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                </svg>
                <span class="font-semibold">Address:</span>
                <span class="ml-2">{{ profile.address }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M8.445 14.832A1 1 0 0010 14v-2.798l5.445-3.63A1 1 0 0015 6H5a1 1 0 00-.445 1.832L10 11.462V14a1 1 0 00-1.555.832z"/>
                </svg>
                <span class="font-semibold">Annual Income:</span>
                <span class="ml-2">₹{{ profile.annualIncome ? profile.annualIncome.toLocaleString('en-IN') : '0' }}</span>
              </div>
            </div>
            
            <!-- Edit Mode -->
            <form v-if="isEditing" @submit.prevent="updateProfile" class="space-y-6">
              <div>
                <label class="block text-gray-600 mb-1">Full Name</label>
                <input
                  v-model="editForm.fullName"
                  class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  :class="{ 'border-red-600': validationErrors.fullName }"
                  required
                />
                <p v-if="validationErrors.fullName" class="text-red-600 text-sm mt-1">{{ validationErrors.fullName }}</p>
              </div>
              
              <div>
                <label class="block text-gray-600 mb-1">Email</label>
                <input
                  v-model="editForm.email"
                  type="email"
                  class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  :class="{ 'border-red-600': validationErrors.email }"
                  required
                />
                <p v-if="validationErrors.email" class="text-red-600 text-sm mt-1">{{ validationErrors.email }}</p>
              </div>
              
              <div>
                <label class="block text-gray-600 mb-1">Phone</label>
                <input
                  v-model="editForm.phone"
                  class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  :class="{ 'border-red-600': validationErrors.phone }"
                  required
                />
                <p v-if="validationErrors.phone" class="text-red-600 text-sm mt-1">{{ validationErrors.phone }}</p>
              </div>
              
              <div>
                <label class="block text-gray-600 mb-1">Address</label>
                <input
                  v-model="editForm.address"
                  class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  :class="{ 'border-red-600': validationErrors.address }"
                  required
                />
                <p v-if="validationErrors.address" class="text-red-600 text-sm mt-1">{{ validationErrors.address }}</p>
              </div>
              
              <div>
                <label class="block text-gray-600 mb-1">Annual Income (₹)</label>
                <input
                  v-model.number="editForm.annualIncome"
                  type="number"
                  min="0"
                  step="1000"
                  class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  :class="{ 'border-red-600': validationErrors.annualIncome }"
                  required
                />
                <p v-if="validationErrors.annualIncome" class="text-red-600 text-sm mt-1">{{ validationErrors.annualIncome }}</p>
              </div>
              
              <div class="flex justify-end space-x-3">
                <button
                  type="submit"
                  :disabled="updating"
                  class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition-colors disabled:opacity-50"
                >
                  {{ updating ? 'Saving...' : 'Save' }}
                </button>
                <button
                  type="button"
                  @click="cancelEdit"
                  class="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-lg transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>

          <!-- Activity Timeline -->
          <div v-if="!loading" class="bg-white rounded-xl shadow-lg p-8">
            <h3 class="text-xl font-semibold text-gray-700 mb-6 border-b pb-4">Activity Timeline</h3>
            <div class="relative pl-8 space-y-8">
              <div class="absolute left-4 top-0 bottom-0 w-0.5 bg-indigo-200"></div>
              <div class="flex items-start">
                <div class="flex-shrink-0 w-6 h-6 bg-indigo-600 rounded-full flex items-center justify-center">
                  <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="ml-4">
                  <p class="font-semibold text-gray-800">Account Created</p>
                  <p class="text-sm text-gray-600">
                    {{ profile.createdAt ? new Date(profile.createdAt).toLocaleString() : 'N/A' }}
                  </p>
                </div>
              </div>
              <div class="flex items-start">
                <div class="flex-shrink-0 w-6 h-6 bg-indigo-600 rounded-full flex items-center justify-center">
                  <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01 detectar .61-1.276z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="ml-4">
                  <p class="font-semibold text-gray-800">Profile Last Updated</p>
                  <p class="text-sm text-gray-600">
                    {{ profile.updatedAt ? new Date(profile.updatedAt).toLocaleString() : 'N/A' }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Success/Error Toast -->
    <div v-if="showToast" class="fixed bottom-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300"
         :class="toastType === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'">
      <div class="flex items-center">
        <svg v-if="toastType === 'success'" class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
        </svg>
        <svg v-else class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
        {{ toastMessage }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProfilePage',
  data() {
    return {
      loading: true,
      updating: false,
      isEditing: false,
      showToast: false,
      toastMessage: '',
      toastType: 'success',
      userId: null,
      profile: {
        userId: null,
        fullName: '',
        email: '',
        phone: '',
        address: '',
        annualIncome: 0,
        isEligibleForBNPL: false,
        createdAt: null,
        updatedAt: null,
      },
      editForm: {
        fullName: '',
        email: '',
        phone: '',
        address: '',
        annualIncome: 0,
      },
      originalProfile: null,
      validationErrors: {
        fullName: '',
        email: '',
        phone: '',
        address: '',
        annualIncome: '',
      },
    };
  },
  computed: {
    completionPercentage() {
      const fields = [
        this.profile.fullName && this.profile.fullName.trim() !== '',
        this.profile.email && this.profile.email.trim() !== '',
        this.profile.phone && this.profile.phone.trim() !== '',
        this.profile.address && this.profile.address.trim() !== '',
        this.profile.annualIncome > 0,
      ];
      const filled = fields.filter(Boolean).length;
      return Math.round((filled / fields.length) * 100);
    },
  },
  async created() {
    this.getUserId();
    if (this.userId) {
      await this.fetchProfile();
    } else {
      this.showToastMessage('Please login to view profile', 'error');
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
    }
  },
  methods: {
    getUserId() {
      try {
        const storedUserId = localStorage.getItem('userId');
        if (storedUserId) {
          this.userId = parseInt(storedUserId, 10);
        }
      } catch (error) {
        console.error('Error reading userId from localStorage:', error);
      }
    },
    
    async fetchProfile() {
      this.loading = true;
      try {
        const response = await fetch(`http://localhost:8089/api/profile/${this.userId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          this.profile = await response.json();
          console.log('Profile loaded:', this.profile);
        } else if (response.status === 404) {
          this.showToastMessage('Profile not found', 'error');
        } else {
          throw new Error('Failed to fetch profile');
        }
      } catch (error) {
        console.error('Error fetching profile:', error);
        this.showToastMessage('Failed to load profile. Please try again.', 'error');
      } finally {
        this.loading = false;
      }
    },

    startEditing() {
      this.originalProfile = { ...this.profile };
      this.editForm = {
        fullName: this.profile.fullName,
        email: this.profile.email,
        phone: this.profile.phone,
        address: this.profile.address,
        annualIncome: this.profile.annualIncome,
      };
      this.isEditing = true;
      this.clearValidationErrors();
    },

    validateForm() {
      this.clearValidationErrors();
      let isValid = true;

      if (!this.editForm.fullName.trim()) {
        this.validationErrors.fullName = 'Full name is required';
        isValid = false;
      }

      if (!this.editForm.email.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/)) {
        this.validationErrors.email = 'Invalid email address';
        isValid = false;
      }

      if (!this.editForm.phone.match(/^\d{10}$/)) {
        this.validationErrors.phone = 'Phone number must be 10 digits';
        isValid = false;
      }

      if (!this.editForm.address.trim()) {
        this.validationErrors.address = 'Address is required';
        isValid = false;
      }

      if (this.editForm.annualIncome <= 0 || isNaN(this.editForm.annualIncome)) {
        this.validationErrors.annualIncome = 'Annual income must be greater than 0';
        isValid = false;
      }

      return isValid;
    },

    async updateProfile() {
      if (!this.validateForm()) return;

      this.updating = true;
      try {
        const response = await fetch(`http://localhost:8089/api/profile/${this.userId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            fullName: this.editForm.fullName,
            email: this.editForm.email,
            phone: this.editForm.phone,
            address: this.editForm.address,
            annualIncome: this.editForm.annualIncome,
          }),
        });

        if (response.ok) {
          this.profile = await response.json();
          this.isEditing = false;
          this.originalProfile = null;
          this.showToastMessage('Profile updated successfully!', 'success');
          
          // Update localStorage if fullName changed
          if (this.profile.fullName) {
            localStorage.setItem('userName', this.profile.fullName);
            localStorage.setItem('fullName', this.profile.fullName);
          }
        } else {
          const errorText = await response.text();
          this.showToastMessage(errorText || 'Failed to update profile', 'error');
        }
      } catch (error) {
        console.error('Error updating profile:', error);
        this.showToastMessage('Failed to update profile. Please try again.', 'error');
      } finally {
        this.updating = false;
      }
    },

    cancelEdit() {
      this.profile = { ...this.originalProfile };
      this.isEditing = false;
      this.originalProfile = null;
      this.clearValidationErrors();
    },

    clearValidationErrors() {
      this.validationErrors = {
        fullName: '',
        email: '',
        phone: '',
        address: '',
        annualIncome: '',
      };
    },

    showToastMessage(message, type = 'success') {
      this.toastMessage = message;
      this.toastType = type;
      this.showToast = true;
      setTimeout(() => {
        this.showToast = false;
      }, 4000);
    },
  },
};
</script>

<style scoped>
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.bg-gradient-to-r {
  background: linear-gradient(to right, #4f46e5, #7c3aed);
}

@media (max-width: 767px) {
  .md\:col-span-2 {
    grid-column: span 1 / span 1;
  }
  .grid-cols-3 {
    grid-template-columns: 1fr;
  }
}
</style>