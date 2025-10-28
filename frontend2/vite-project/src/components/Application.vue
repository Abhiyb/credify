<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar title="My Applications" />

    <!-- Main Content with padding to avoid overlap -->
    <div class="pt-32 md:pt-28">
      <div class="max-w-7xl mx-auto px-6">
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center items-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <span class="ml-3 text-gray-600">Loading your applications...</span>
        </div>

        <!-- Applications Content -->
        <div v-else>
          <div class="flex justify-between items-center mb-8">
            <h1 class="text-2xl font-semibold text-gray-900">Your Credit Card Applications</h1>
            <button 
              @click="refreshApplications"
              :disabled="loading"
              class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              {{ loading ? 'Loading...' : 'Refresh' }}
            </button>
          </div>

          <!-- Error State -->
          <div v-if="errorMsg" class="mb-6">
            <div class="bg-red-50 border border-red-200 rounded-md p-4">
              <div class="flex items-center">
                <svg class="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                </svg>
                <p class="text-red-800 font-medium">{{ errorMsg }}</p>
              </div>
            </div>
          </div>

          <!-- Applications List -->
          <div v-if="!errorMsg && applications.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="(app, idx) in applications"
              :key="app.id || idx"
              class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow max-w-3xl mx-auto"
            >
              <div class="flex justify-between items-start">
                <div class="flex-1">
                  <div class="flex items-center mb-4">
                    <h3 class="text-lg font-semibold text-blue-700 mr-3">{{ app.cardType || 'Unknown Card Type' }}</h3>
                    <span
                      :class="['px-3 py-1 rounded-full font-medium text-sm inline-flex items-center', getStatusClass(app.status)]"
                    >
                      <span class="mr-1">{{ getStatusIcon(app.status) }}</span>
                      {{ app.status || 'Unknown' }}
                    </span>
                  </div>
                  
                  <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600 mb-4">
                    <div>
                      <span class="font-medium text-gray-700">Requested Limit:</span>
                      <p class="text-gray-900 font-semibold">{{ formatCurrency(app.requestedLimit) }}</p>
                    </div>
                    
                    <div>
                      <span class="font-medium text-gray-700">Application Date:</span>
                      <p class="text-gray-900">{{ formatDate(app.applicationDate) }}</p>
                    </div>
                    
                    <div v-if="app.processedDate">
                      <span class="font-medium text-gray-700">Processed Date:</span>
                      <p class="text-gray-900">{{ formatDate(app.processedDate) }}</p>
                    </div>
                  </div>

                  <!-- Additional Details -->
                  <div v-if="app.approvedLimit && app.status?.toUpperCase() === 'APPROVED'" class="mt-4 p-3 bg-green-50 rounded-md border border-green-200">
                    <p class="text-green-800 font-medium">
                      🎉 Congratulations! Your application has been approved with a credit limit of {{ formatCurrency(app.approvedLimit) }}
                    </p>
                  </div>

                  <div v-if="app.rejectionReason && app.status?.toUpperCase() === 'REJECTED'" class="mt-4 p-3 bg-red-50 rounded-md border border-red-200">
                    <p class="text-red-800 font-medium">
                      <span class="font-semibold">Rejection Reason:</span> {{ app.rejectionReason }}
                    </p>
                  </div>

                  <div v-if="app.remarks" class="mt-4 p-3 bg-blue-50 rounded-md border border-blue-200">
                    <p class="text-blue-800">
                      <span class="font-semibold">Remarks:</span> {{ app.remarks }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-if="!errorMsg && applications.length === 0" class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="text-lg font-medium text-gray-900 mb-2">No Applications Found</h3>
            <p class="text-gray-500 mb-4">You haven't submitted any credit card applications yet.</p>
            <router-link 
              to="/apply-card" 
              class="inline-block px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors duration-200"
            >
              Apply for Credit Card
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import Navbar from './Navbar.vue';

const router = useRouter();
const applications = ref([]);
const loading = ref(true);
const errorMsg = ref(null);

// Get user ID from localStorage
const userId = computed(() => {
  const storedUserId = localStorage.getItem('userId');
  return storedUserId ? parseInt(storedUserId) : null;
});

const fetchApplications = async () => {
  if (!userId.value) {
    errorMsg.value = 'User not logged in. Please log in to view your applications.';
    loading.value = false;
    router.push('/login');
    return;
  }

  loading.value = true;
  errorMsg.value = null;
  
  try {
    const res = await axios.get(`http://localhost:8080/cards/applications/${userId.value}`);
    applications.value = res.data || [];
  } catch (error) {
    console.error('Error fetching applications:', error);
    if (error.response?.status === 400 || error.response?.status === 404) {
      // Treat 400 or 404 as "no applications found"
      applications.value = [];
    } else if (error.response?.status === 401) {
      errorMsg.value = 'Unauthorized access. Please log in again.';
      router.push('/login');
    } else {
      errorMsg.value = error.response?.data?.message || error.message || 'Failed to fetch applications.';
    }
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return 'N/A';
  try {
    return new Date(dateStr).toLocaleDateString('en-IN', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  } catch (error) {
    return 'Invalid Date';
  }
};

const formatCurrency = (amount) => {
  if (!amount && amount !== 0) return 'N/A';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount);
};

const getStatusClass = (status) => {
  if (!status) return 'bg-gray-100 text-gray-800';
  
  switch (status.toUpperCase()) {
    case 'APPROVED': 
      return 'bg-green-100 text-green-800 border border-green-200';
    case 'PENDING': 
      return 'bg-yellow-100 text-yellow-800 border border-yellow-200';
    case 'REJECTED': 
      return 'bg-red-100 text-red-800 border border-red-200';
    case 'UNDER_REVIEW':
      return 'bg-blue-100 text-blue-800 border border-blue-200';
    default: 
      return 'bg-gray-100 text-gray-800 border border-gray-200';
  }
};

const getStatusIcon = (status) => {
  if (!status) return '●';
  
  switch (status.toUpperCase()) {
    case 'APPROVED': return '✓';
    case 'PENDING': return '⏳';
    case 'REJECTED': return '✗';
    case 'UNDER_REVIEW': return '👁';
    default: return '●';
  }
};

const refreshApplications = () => {
  fetchApplications();
};

onMounted(() => {
  if (userId.value) {
    fetchApplications();
  } else {
    router.push('/login');
  }
});
</script>

<style scoped>
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>