<template>
  <div class="min-h-screen bg-gray-100 font-sans">
    <!-- Hero Section -->
    <div class="relative bg-gradient-to-r from-indigo-600 to-purple-600 h-32 md:h-40 overflow-visible">
      <div class="w-full h-full bg-gradient-to-r from-indigo-600 to-purple-600 opacity-80"></div>
    </div>
<!-- Floating / Fixed Back to Dashboard Button - Top Left Corner -->
<!-- Add right after the hero div, before main content -->
<button
  @click="goToDashboard"
  class="fixed top-6 left-6 z-50 flex items-center gap-2.5 px-5 py-2.5 rounded-full bg-white/95 backdrop-blur-md shadow-md border border-indigo-100 hover:border-indigo-300 hover:bg-indigo-50/80 transition-all duration-300 group"
>
  <svg 
    class="w-5 h-5 text-indigo-700 group-hover:text-indigo-900 transform group-hover:-translate-x-0.5 transition-transform" 
    fill="none" 
    stroke="currentColor" 
    viewBox="0 0 24 24"
  >
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
  </svg>
  <span class="font-medium text-indigo-800 group-hover:text-indigo-950 text-sm md:text-base">Back to Dashboard</span>
</button>
    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 pb-12 relative z-20">
      <div v-if="loading" class="flex justify-center items-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        <span class="ml-3 text-gray-600">Loading profile...</span>
      </div>

      <div v-else-if="error" class="text-center py-20">
        <p class="text-red-600 text-xl font-semibold mb-6">{{ error }}</p>
        <button 
          @click="logoutAndRedirect"
          class="bg-red-600 hover:bg-red-700 text-white px-8 py-3 rounded-lg font-medium transition-colors"
        >
          Sign In Again
        </button>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Left Sidebar -->
        <div class="space-y-8">
          <!-- Profile Picture & Basic Info -->
          <div class="bg-white rounded-xl shadow-lg p-8 text-center">
            <div class="mx-auto w-32 h-32 rounded-full border-4 border-white shadow-md mb-4 relative">
              <div class="w-full h-full flex items-center justify-center bg-gray-200 rounded-full text-3xl font-bold text-gray-500">
                {{ profile.fullName ? profile.fullName.charAt(0).toUpperCase() : '?' }}
              </div>
            </div>
            <h2 class="text-2xl font-bold text-gray-800">{{ profile.fullName || 'User' }}</h2>
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
          <!-- Editable Profile Details -->
          <div class="bg-white rounded-xl shadow-lg p-8">
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
                <span class="ml-2">{{ profile.fullName || 'Not set' }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                  <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                </svg>
                <span class="font-semibold">Email:</span>
                <span class="ml-2">{{ profile.email || 'Not set' }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                </svg>
                <span class="font-semibold">Phone:</span>
                <span class="ml-2">{{ profile.phone || 'Not set' }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                </svg>
                <span class="font-semibold">Address:</span>
                <span class="ml-2">{{ profile.address || 'Not set' }}</span>
              </div>
              <div class="flex items-center">
                <svg class="w-5 h-5 text-indigo-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M8.445 14.832A1 1 0 0010 14v-2.798l5.445-3.63A1 1 0 0015 6H5a1 1 0 00-.445 1.832L10 11.462V14a1 1 0 00-1.555.832z"/>
                </svg>
                <span class="font-semibold">Annual Income:</span>
                <span class="ml-2">₹{{ profile.annualIncome ? profile.annualIncome.toLocaleString('en-IN') : 'Not set' }}</span>
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
          <div class="bg-white rounded-xl shadow-lg p-8">
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
                    <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd"/>
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

    <!-- Toast Notification -->
    <div v-if="showToast" class="fixed bottom-6 right-6 px-6 py-3 rounded-lg shadow-2xl z-50 transform transition-all duration-300"
         :class="toastType === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white'">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const API_BASE_URL = 'http://localhost:8080';

const loading = ref(true);
const updating = ref(false);
const isEditing = ref(false);
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const error = ref(null);

const profile = ref({
  fullName: '',
  email: '',
  phone: '',
  address: '',
  annualIncome: 0,
  isEligibleForBNPL: false,
  createdAt: null,
  updatedAt: null,
});

const editForm = ref({
  fullName: '',
  phone: '',
  address: '',
  annualIncome: 0,
});

const validationErrors = ref({
  fullName: '',
  phone: '',
  address: '',
  annualIncome: '',
});

const originalProfile = ref(null);

// Get token from either storage
const getAuthToken = () => localStorage.getItem('authToken') || sessionStorage.getItem('authToken');

// Logout
const logoutAndRedirect = () => {
  localStorage.removeItem('authToken');
  sessionStorage.removeItem('authToken');
  router.push('/login');
};

// Toast
const showToastMessage = (msg, type = 'success') => {
  toastMessage.value = msg;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => showToast.value = false, 4000);
};

// Clear errors
const clearValidationErrors = () => {
  validationErrors.value = {};
};

// Start edit
const startEditing = () => {
  originalProfile.value = { ...profile.value };
  editForm.value = {
    fullName: profile.value.fullName,
    phone: profile.value.phone,
    address: profile.value.address,
    annualIncome: profile.value.annualIncome,
  };
  isEditing.value = true;
  clearValidationErrors();
};

// Cancel edit
const cancelEdit = () => {
  profile.value = { ...originalProfile.value };
  isEditing.value = false;
  originalProfile.value = null;
  clearValidationErrors();
};

// Validate form
const validateForm = () => {
  clearValidationErrors();
  let isValid = true;

  if (!editForm.value.fullName.trim()) {
    validationErrors.value.fullName = 'Full name is required';
    isValid = false;
  }

  const phoneRegex = /^\d{10}$/;
  if (!editForm.value.phone || !phoneRegex.test(editForm.value.phone)) {
    validationErrors.value.phone = 'Phone must be exactly 10 digits';
    isValid = false;
  }

  if (!editForm.value.address.trim()) {
    validationErrors.value.address = 'Address is required';
    isValid = false;
  }

  if (!editForm.value.annualIncome || editForm.value.annualIncome < 0) {
    validationErrors.value.annualIncome = 'Annual income must be 0 or more';
    isValid = false;
  }

  return isValid;
};

// Update profile + re-fetch
const updateProfile = async () => {
  if (!validateForm()) return;

  updating.value = true;
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/api/profile/me`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(editForm.value),
    });

    if (!response.ok) {
      if (response.status === 401) throw new Error('unauthorized');
      const errText = await response.text();
      throw new Error(errText || 'Failed to update profile');
    }

    showToastMessage('Profile updated successfully!', 'success');

    // Re-fetch fresh profile data from backend
    await fetchProfile();

    isEditing.value = false;
    originalProfile.value = null;
  } catch (err) {
    console.error('Update error:', err);
    if (err.message.includes('unauthorized')) {
      showToastMessage('Session expired. Please sign in.', 'error');
      setTimeout(logoutAndRedirect, 2000);
    } else {
      showToastMessage(err.message || 'Update failed', 'error');
    }
  } finally {
    updating.value = false;
  }
};

// Fetch profile
const fetchProfile = async () => {
  const token = getAuthToken();
  if (!token) {
    error.value = 'Please sign in to view your profile.';
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    const response = await fetch(`${API_BASE_URL}/api/profile/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!response.ok) {
      if (response.status === 401) throw new Error('unauthorized');
      throw new Error('Failed to load profile');
    }

    const data = await response.json();
    profile.value = {
      fullName: data.fullName || '',
      email: data.email || '',
      phone: data.phone || '',
      address: data.address || '',
      annualIncome: data.annualIncome || 0,
      isEligibleForBNPL: data.isEligibleForBNPL || false,
      createdAt: data.createdAt || null,
      updatedAt: data.updatedAt || null,
    };
  } catch (err) {
    if (err.message.includes('unauthorized')) {
      error.value = 'Session expired. Please sign in.';
      setTimeout(logoutAndRedirect, 2000);
    } else {
      error.value = 'Failed to load profile.';
    }
  } finally {
    loading.value = false;
  }
};
const goToDashboard = () => {
  router.push('/dashboard');
};
onMounted(() => {
  if (!getAuthToken()) {
    router.push('/login');
  } else {
    fetchProfile();
  }
});
</script>

<style scoped>
/* Keep your original styles */
</style>