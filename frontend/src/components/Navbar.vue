<template>
  <header :class="[
    'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
    scrolled ? 'bg-white shadow-md py-3' : 'bg-transparent py-6'
  ]">
    <div class="container mx-auto px-6 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center space-x-2">
        <div class="h-8 w-8 rounded-full bg-gradient-to-r from-blue-500 to-indigo-500 flex items-center justify-center">
          <span class="text-white font-bold text-lg">C</span>
        </div>
        
        <router-link to="/dashboard" class="text-xl font-bold text-gray-800">Credify</router-link>

      </div>

      <!-- Desktop Navigation -->
      <nav class="hidden md:flex items-center space-x-8">
        <button
          v-for="item in navItems"
          :key="item.name"
          @click="handleNavClick(item)"
          class="text-gray-700 hover:text-blue-500 transition-colors duration-200"
          :class="{ '!text-blue-600 font-semibold': item.route === route.path }"
        >
          {{ item.name }}
        </button>
      </nav>

      <div class="flex items-center space-x-4">
        <!-- Profile Dropdown (desktop only) -->
        <div class="relative hidden md:block">
          <button
            @click="toggleDropdown"
            class="flex items-center space-x-2 bg-gray-100 hover:bg-gray-200 rounded-full px-4 py-2 transition-colors duration-200"
          >
            <div class="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center text-gray-600">
              <User class="w-4 h-4" />
            </div>
            <span class="text-sm font-medium text-gray-700">{{ userName }}</span>
            <ChevronDown class="w-4 h-4 text-gray-600" />
          </button>

          <div
            v-if="isDropdownOpen"
            class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-20"
          >
            <button
              @click="handleProfileClick"
              class="flex items-center space-x-3 w-full px-4 py-2 text-left text-gray-700 hover:text-blue-500 transition-colors duration-200"
            >
              <User class="w-4 h-4" />
              <span>Profile</span>
            </button>
            <button
              @click="handleLogoutClick"
              class="flex items-center space-x-3 w-full px-4 py-2 text-left text-gray-700 hover:text-blue-500 transition-colors duration-200"
            >
              <LogOut class="w-4 h-4" />
              <span>Logout</span>
            </button>
          </div>
        </div>

        <!-- Hamburger Menu (mobile only) -->
        <button
          @click="toggleMobileMenu"
          class="md:hidden text-gray-800 focus:outline-none"
        >
          <component :is="isMobileMenuOpen ? X : Menu" class="w-6 h-6" />
        </button>
      </div>
    </div>

    <!-- Mobile Menu -->
    <div v-if="isMobileMenuOpen" class="md:hidden bg-white absolute top-full left-0 right-0 shadow-lg py-4 px-6 transition-all duration-300">
      <nav class="flex flex-col space-y-4">
        <button
          v-for="item in navItems"
          :key="item.name"
          @click="handleNavClick(item)"
          class="text-gray-700 hover:text-blue-500 transition-colors duration-200 py-2 text-left"
          :class="{ 'text-blue-600 font-semibold': item.route === route.path }"
        >
          {{ item.name }}
        </button>
        <button
          @click="handleProfileClick"
          class="text-gray-700 hover:text-blue-500 transition-colors duration-200 py-2 text-left flex items-center space-x-2"
          :class="{ 'text-blue-600 font-semibold': route.path === '/profile' }"
        >
          <User class="w-4 h-4" />
          <span>Profile</span>
        </button>
        <button
          @click="handleLogoutClick"
          class="text-gray-700 hover:text-blue-500 transition-colors duration-200 py-2 text-left flex items-center space-x-2"
        >
          <LogOut class="w-4 h-4" />
          <span>Logout</span>
        </button>
      </nav>
    </div>

    <!-- Success Toast -->
    <div v-if="showToast" class="fixed bottom-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50">
      <div class="flex items-center">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
        </svg>
        {{ toastMessage }}
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { User, LogOut, ChevronDown, Menu, X } from 'lucide-vue-next';

// Props
const props = defineProps({
  title: {
    type: String,
    default: 'Dashboard'
  }
});

// Router
const router = useRouter();
const route = useRoute();

// Navigation items
const navItems = [
  { name: 'Home', route: '/dashboard' },
  { name: 'My Cards', route: '/card' },
  { name: 'Applications', route: '/applications' },
  { name: 'Transactions', route: '/transactions' }
];

// Reactive state
const scrolled = ref(false);
const isDropdownOpen = ref(false);
const isMobileMenuOpen = ref(false);
const showToast = ref(false);
const toastMessage = ref('');
const userName = ref('');

// Get user name from localStorage
const getUserName = () => {
  try {
    const fullName = localStorage.getItem('fullName');
    console.log('Full Name from localStorage:', localStorage);
    userName.value = fullName || 'Guest';
  } catch (err) {
    console.error('Error reading fullName from localStorage:', err);
    userName.value = 'Guest';
  }
};

// Navigation functions
const handleNavClick = (item) => {
  isMobileMenuOpen.value = false;
  isDropdownOpen.value = false;
  router.push(item.route);
};

const handleProfileClick = () => {
  isDropdownOpen.value = false;
  isMobileMenuOpen.value = false;
  router.push('/profile');
};

const handleLogoutClick = () => {
  localStorage.removeItem('userId');
  localStorage.removeItem('userName');
  localStorage.removeItem('fullName');
  localStorage.removeItem('currentUser');
  localStorage.removeItem('authToken');

  userName.value = 'Guest'; // Reset userName immediately
  showToastMessage('Logged out successfully');

  setTimeout(() => {
    router.push('/');
  }, 1000);

  isDropdownOpen.value = false;
  isMobileMenuOpen.value = false;
};

// Utility functions
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
  isMobileMenuOpen.value = false;
};

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
  isDropdownOpen.value = false;
};

const showToastMessage = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// Handle scroll for background change
const handleScroll = () => {
  scrolled.value = window.scrollY > 50;
};

// Close dropdowns when clicking outside
const handleClickOutside = (event) => {
  if (!event.target.closest('.relative')) {
    isDropdownOpen.value = false;
  }
};

// Initialize component
onMounted(() => {
  getUserName();
  window.addEventListener('scroll', handleScroll);
  document.addEventListener('click', handleClickOutside);
});

// Cleanup
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  document.removeEventListener('click', handleClickOutside);
});
</script>