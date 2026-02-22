<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar title="Dashboard" />

    <!-- Main Content with increased padding to create a larger gap -->
    <div class="pt-32 md:pt-28">
      <div class="max-w-7xl mx-auto px-6">
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center items-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <span class="ml-3 text-gray-600">Loading dashboard...</span>
        </div>

        <!-- Error State (e.g. unauthorized or network issue) -->
        <div v-else-if="error" class="text-center py-20">
          <p class="text-red-600 text-xl font-semibold mb-6">{{ error }}</p>
          <button 
            @click="logoutAndRedirect"
            class="bg-red-600 hover:bg-red-700 text-white px-8 py-3 rounded-lg font-medium transition-colors"
          >
            Sign In Again
          </button>
        </div>

        <!-- Dashboard Content (exact same as your original) -->
        <div v-else>
          <!-- Dashboard Cards -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Active Cards -->
            <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-6 border border-blue-200">
              <h3 class="text-gray-700 font-semibold mb-2">Active Cards</h3>
              <div class="text-4xl font-bold text-blue-600 mb-2">{{ dashboardData.activeCards }}</div>
              <p class="text-gray-600 text-sm">{{ dashboardData.lastUsed }}</p>
            </div>

            <!-- Available Credit -->
            <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-6 border border-green-200">
              <h3 class="text-gray-700 font-semibold mb-2">Available Credit</h3>
              <div class="text-4xl font-bold text-green-600 mb-2">
                ₹{{ dashboardData.availableCredit.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}
              </div>
              <p class="text-gray-600 text-sm">
                Total limit: ₹{{ dashboardData.totalLimit.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}
              </p>
            </div>

            <!-- Pending Applications -->
            <div class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-6 border border-orange-200">
              <h3 class="text-gray-700 font-semibold mb-2">Pending Applications</h3>
              <div class="text-4xl font-bold text-orange-600 mb-2">{{ dashboardData.pendingApplications }}</div>
              <p class="text-gray-600 text-sm">Applications in review</p>
            </div>

            <!-- Recent Transactions -->
            <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-6 border border-purple-200">
              <h3 class="text-gray-700 font-semibold mb-2">Recent Transactions</h3>
              <div class="text-4xl font-bold text-purple-600 mb-2">{{ dashboardData.recentTransactions }}</div>
              <p class="text-gray-600 text-sm">This month</p>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="mt-8 bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-xl font-semibold text-gray-800 mb-4">Quick Actions</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <button 
                @click="navigateToApplyCard"
                class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition-colors duration-200 w-full"
              >
                Apply for New Card
              </button>
              <button 
                @click="navigateToPayment"
                class="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg font-medium transition-colors duration-200 w-full"
              >
                Make Payment
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Navbar from './Navbar.vue'; // adjust path if needed

const router = useRouter();
const API_BASE_URL = 'http://localhost:8080';

const loading = ref(true);
const error = ref(null);

const dashboardData = ref({
  activeCards: 0,
  availableCredit: 0,
  totalLimit: 0,
  pendingApplications: 0,
  recentTransactions: 0,
  lastUsed: 'No recent activity'
});

// Get auth token
const getAuthToken = () => {
  return localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
};

// Logout & redirect to login
const logoutAndRedirect = () => {
  // Clear tokens
  localStorage.removeItem('authToken');
  sessionStorage.removeItem('authToken');

  // Use replace instead of push (replaces current history entry)
  router.replace('/login')
    .then(() => {
      console.log('Logout redirect success → /login');
    })
    .catch(err => {
      console.error('Router replace failed:', err);
      // Strong fallback: force browser navigation
      window.location.href = '/login';
    });
};

// Quick navigation
const navigateToApplyCard = () => router.push('/apply-card');
const navigateToPayment = () => router.push('/transactions');

// Fetch dashboard data using JWT
const fetchDashboardData = async () => {
  const token = getAuthToken();
  if (!token) {
    error.value = 'Please sign in to view your dashboard.';
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = null;

  const headers = {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };

  try {
    // 1. Get my cards - /api/cards/me
    const cardsRes = await fetch(`${API_BASE_URL}/api/cards/me`, { headers });
    if (!cardsRes.ok) {
      if (cardsRes.status === 401) throw new Error('unauthorized');
      throw new Error('Failed to load cards');
    }
    const cards = await cardsRes.json();

    const active = cards.filter(c => c.status === 'ACTIVE');
    dashboardData.value.activeCards = active.length;

    if (active.length > 0) {
      const total = active.reduce((sum, c) => sum + (parseFloat(c.creditLimit) || 0), 0);
      const avail = active.reduce((sum, c) => sum + (parseFloat(c.availableLimit) || 0), 0);

      dashboardData.value.totalLimit = total;
      dashboardData.value.availableCredit = avail;
      dashboardData.value.lastUsed = 'Last used recently';
    } else {
      dashboardData.value.lastUsed = 'No active cards';
    }

    // 2. Get my applications - /api/cards/applications/me
    const appsRes = await fetch(`${API_BASE_URL}/api/cards/applications/me`, { headers });
    if (appsRes.ok) {
      const apps = await appsRes.json();
      dashboardData.value.pendingApplications = apps.filter(a => 
        ['PENDING', 'UNDER_REVIEW', 'IN_PROGRESS'].includes(a.status)
      ).length;
    }

    // 3. Transactions this month - loop over cards
    let txCount = 0;
    for (const card of cards) {
      const txRes = await fetch(`${API_BASE_URL}/api/transactions/card/${card.cardId}`, { headers });
      if (txRes.ok) {
        const txs = await txRes.json();
        const now = new Date();
        txCount += txs.filter(t => {
          const d = new Date(t.transactionDate || t.createdAt || t.date);
          return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
        }).length;
      }
    }
    dashboardData.value.recentTransactions = txCount;

  } catch (err) {
    console.error('Dashboard fetch error:', err);
    if (err.message.includes('unauthorized') || err.message.includes('401')) {
      error.value = 'Your session has expired. Please sign in again.';
      setTimeout(logoutAndRedirect, 1800);
    } else {
      error.value = 'Unable to load dashboard data. Please try again later.';
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  const token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
  if (!token) {
    router.push('/login');
  } else {
    fetchDashboardData();
  }
});
</script>

<style scoped>
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}
</style>