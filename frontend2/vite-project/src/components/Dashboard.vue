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

        <!-- Dashboard Content -->
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
              <div class="text-4xl font-bold text-green-600 mb-2">₹{{ dashboardData.availableCredit.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</div>
              <p class="text-gray-600 text-sm">Total limit: ₹{{ dashboardData.totalLimit.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</p>
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
import Navbar from './Navbar.vue';

const router = useRouter();
const API_BASE_URL = 'http://localhost:8080';

const loading = ref(false);

// User data
const userId = ref(null);

// Dashboard data
const dashboardData = ref({
  activeCards: 0,
  availableCredit: 0,
  totalLimit: 0,
  pendingApplications: 0,
  recentTransactions: 0,
  lastUsed: 'No recent activity'
});

// Get user ID from localStorage
const getUserIdFromStorage = () => {
  try {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      userId.value = parseInt(storedUserId, 10);
      return true;
    }
    return false;
  } catch (err) {
    console.error('Error reading userId from localStorage:', err);
    return false;
  }
};

// Fetch dashboard data from backend
const fetchDashboardData = async () => {
  if (!userId.value) return;

  loading.value = true;
  try {
    const cardsResponse = await fetch(`${API_BASE_URL}/api/cards/${userId.value}`);
    let cards = [];
    let allCardIds = [];
    
    if (cardsResponse.ok) {
      cards = await cardsResponse.json();
      allCardIds = cards.map(card => card.cardId);
      
      const activeCards = cards.filter(card => card.status === 'ACTIVE');
      dashboardData.value.activeCards = activeCards.length;
      
      const totalLimit = activeCards.reduce((sum, card) => {
        return sum + (parseFloat(card.creditLimit) || 0);
      }, 0);
      
      const availableCredit = activeCards.reduce((sum, card) => {
        return sum + (parseFloat(card.availableLimit) || 0);
      }, 0);
      
      dashboardData.value.totalLimit = totalLimit;
      dashboardData.value.availableCredit = availableCredit;
      
      if (activeCards.length > 0) {
        dashboardData.value.lastUsed = 'Last used: 2 days ago';
      } else {
        dashboardData.value.lastUsed = 'No active cards';
      }
    }

    try {
      const applicationsResponse = await fetch(`${API_BASE_URL}/cards/applications/${userId.value}`);
      if (applicationsResponse.ok) {
        const applications = await applicationsResponse.json();
        const pendingCount = applications.filter(app => 
          app.status === 'PENDING' || app.status === 'UNDER_REVIEW' || app.status === 'IN_PROGRESS'
        ).length;
        dashboardData.value.pendingApplications = pendingCount;
      }
    } catch (appError) {
      console.error('Error fetching applications:', appError);
      dashboardData.value.pendingApplications = 0;
    }

    try {
  let totalTransactionsThisMonth = 0;
  for (const cardId of allCardIds) {
    const transactionsResponse = await fetch(`${API_BASE_URL}/transactions/card/${cardId}`);
    if (transactionsResponse.ok) {
      const transactions = await transactionsResponse.json();
      const currentMonth = new Date().getMonth();
      const currentYear = new Date().getFullYear();
      const thisMonthTransactions = transactions.filter(transaction => {
        // Use transactionDate if available, otherwise use card.user.createdAt
        const dateToUse = transaction.transactionDate || transaction.card?.user?.createdAt;
        if (!dateToUse) return false; // Skip if no valid date is available
        const transactionDate = new Date(dateToUse);
        return transactionDate.getMonth() === currentMonth && 
               transactionDate.getFullYear() === currentYear;
      });
      totalTransactionsThisMonth += thisMonthTransactions.length;
    }
  }
  dashboardData.value.recentTransactions = totalTransactionsThisMonth;
} catch (transError) {
  console.error('Error fetching transactions:', transError);
  dashboardData.value.recentTransactions = 0;
} finally {
  loading.value = false;
}
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
  } finally {
    loading.value = false;
  }
};

// Quick action functions
const navigateToApplyCard = () => {
  router.push('/apply-card');
};

const navigateToPayment = () => {
  router.push('/transactions');
};

// Initialize component
onMounted(() => {
  if (getUserIdFromStorage()) {
    fetchDashboardData();
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