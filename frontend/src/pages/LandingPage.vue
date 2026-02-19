<template>

  <div>
    <!-- Navigation -->
    <nav :class="[
      'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
      scrolled ? 'bg-white shadow-md py-3' : 'bg-transparent py-6'
    ]">
                  
  <div class="container mx-auto px-6 flex items-center justify-between">
        <!-- Logo (scroll to top) -->
        <a href="#" @click.prevent="scrollToTop" class="flex items-center space-x-2">
          <div class="h-8 w-8 rounded-full bg-gradient-to-r from-blue-500 to-indigo-500 flex items-center justify-center">
            <span class="text-white font-bold text-lg">C</span>
          </div>
          <span class="text-xl font-bold">Credify</span>
        </a>

        <!-- Desktop Menu -->
        <div class="hidden md:flex items-center space-x-8">
          <a href="#" @click.prevent="scrollToSection('features')" class="text-gray-700 hover:text-blue-500 transition-colors">Features</a>
          <a href="#" @click.prevent="scrollToSection('dashboard')" class="text-gray-700 hover:text-blue-500 transition-colors">Dashboard</a>
          <a href="#" @click.prevent="router.push('/login')" class="text-blue-700 hover:text-blue-500 transition-colors">Log In</a>
          <button @click="router.push('/register')" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">Get Started</button>
        </div>

        <!-- Mobile Menu Button -->
        <button @click="toggleMobileMenu" class="md:hidden text-gray-800 focus:outline-none">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path v-if="!mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <!-- Mobile Menu -->
      <div v-if="mobileMenuOpen" class="md:hidden bg-white absolute top-full left-0 right-0 shadow-lg py-4 px-6 transition-all duration-300">
        <div class="flex flex-col space-y-4">
          <a @click="scrollToSection('features'), mobileMenuOpen = false" href="#" class="text-gray-700 hover:text-blue-500 transition-colors py-2">Features</a>
          <a @click="scrollToSection('dashboard'), mobileMenuOpen = false" href="#" class="text-gray-700 hover:text-blue-500 transition-colors py-2">Dashboard</a>
          <a @click="router.push('/login'), mobileMenuOpen = false" href="#" class="text-gray-700 hover:text-blue-500 transition-colors py-2">Log In</a>
          <button @click="router.push('/register'), mobileMenuOpen = false" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors w-full">Get Started</button>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="pt-32 pb-24 md:pt-40 md:pb-32 overflow-hidden flex items-center justify-center">
    <div class="container mx-auto px-8">
      <div class="flex flex-col lg:flex-row items-center justify-center">

        <!-- Left Content -->
        <div
          class="w-full lg:w-1/2 text-center mb-12 lg:mb-0 transition-all duration-800"
          :class="isHeroLoaded ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-8'"
        >
          <h1 class="text-4xl md:text-5xl font-bold mb-6 text-center">
            Simplify Your <span class="text-blue-500">Credit Life.</span>
          </h1>

          <p class="text-xl text-gray-600 mb-8 max-w-lg mx-auto">
            All your cards. One powerful dashboard. Always supported by your smart assistant.
          </p>

          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <button
              @click="router.push('/register')"
              class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Get Started Now →
            </button>

            <button
              @click="scrollToSection('features')"
              class="px-6 py-3 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Learn More
            </button>
          </div>
        </div>

        <!-- Right Cards -->
        <div
          class="w-full lg:w-1/2 transition-all duration-800"
          :class="isHeroLoaded ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-8'"
        >
          <div class="relative mx-auto max-w-md">

            <!-- Floating Card -->
            <div class="relative animate-float z-20">
              <div class="w-full h-56 bg-gradient-to-r from-blue-600 to-indigo-500 rounded-2xl shadow-xl p-6 transform rotate-6">
                <div class="flex justify-between items-start">
                  <div>
                    <div class="text-white text-xs uppercase tracking-wider mb-5 opacity-80">Premium Card</div>
                    <div class="text-white text-base mb-6">**** **** **** 1234</div>
                    <div class="text-white text-xs">Valid thru: 05/28</div>
                  </div>
                  <div class="flex flex-col items-end">
                    <svg width="50" height="30" viewBox="0 0 50 30" fill="none">
                      <circle cx="15" cy="15" r="15" fill="#FF5F00" opacity="0.8" />
                      <circle cx="35" cy="15" r="15" fill="#EB001B" opacity="0.8" />
                    </svg>
                  </div>
                </div>

                <div class="mt-6 flex justify-between items-end">
                  <div class="text-white text-sm">John Doe</div>
                  <div class="text-white text-xs uppercase tracking-wider">VISA</div>
                </div>
              </div>
            </div>

            <!-- Background Card -->
            <div class="absolute top-12 -left-4 z-10">
              <div class="w-full h-56 bg-gradient-to-r from-gray-700 to-gray-900 rounded-2xl shadow-lg p-6 transform -rotate-6">
                <div class="flex justify-between items-start">
                  <div>
                    <div class="text-white text-xs uppercase tracking-wider mb-5 opacity-80">Standard Card</div>
                    <div class="text-white text-base mb-6">**** **** **** 5678</div>
                    <div class="text-white text-xs">Valid thru: 09/26</div>
                  </div>

                  <svg width="50" height="30" viewBox="0 0 50 30" fill="none">
                    <rect width="50" height="30" rx="4" fill="#1434CB" opacity="0.8" />
                  </svg>
                </div>

                <div class="mt-6 flex justify-between items-end">
                  <div class="text-white text-sm">John Doe</div>
                  <div class="text-white text-xs uppercase tracking-wider">MASTERCARD</div>
                </div>
              </div>
            </div>

            <!-- Glow Background -->
            <div class="absolute -bottom-10 -right-10 w-64 h-64 rounded-full bg-gradient-to-r from-blue-100 to-indigo-100 opacity-70 blur-3xl z-0"></div>

          </div>
        </div>

      </div>
    </div>
  </section>

    <!-- Features Section -->
    <section id="features" class="py-20 bg-gray-50" ref="featuresRef">
      <div class="container mx-auto px-6">
        <div class="text-center mb-16">
          <h2 class="text-3xl md:text-4xl font-bold mb-4">
            Smart Features for Smarter Credit Management
          </h2>
          <p class="text-xl text-gray-600 max-w-3xl mx-auto">
            Our platform gives you complete control over your credit cards with tools designed for simplicity and efficiency.
          </p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="bg-white p-6 rounded-xl shadow-lg transition-all duration-500 hover:scale-105"
            :class="{ 'opacity-100 translate-y-0': isFeaturesInView, 'opacity-0 translate-y-10': !isFeaturesInView }"
            :style="{ transitionDelay: `${index * 0.1 + 0.1}s` }"
          >
            <div class="w-14 h-14 rounded-full mb-6 flex items-center justify-center bg-gradient-to-br" :class="feature.color">
              <svg v-if="feature.icon === 'application'" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
              <svg v-else-if="feature.icon === 'limit'" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h8m-8 6h16"></path>
              </svg>
              <svg v-else-if="feature.icon === 'secure'" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
              </svg>
              <svg v-else-if="feature.icon === 'track'" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-bold mb-3">{{ feature.title }}</h3>
            <p class="text-gray-600">{{ feature.description }}</p>
          </div>
        </div>
        <div class="mt-16 text-center">
          <a href="#" @click.prevent="scrollToSection('dashboard')" class="inline-flex items-center text-blue-500 hover:text-blue-600 font-medium">
            Learn more about our features
            <svg class="w-5 h-5 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
            </svg>
          </a>
        </div>
      </div>
    </section>

    <!-- Dashboard Section -->
    <section id="dashboard" class="py-20 overflow-hidden" ref="dashboardRef">
      <div class="container mx-auto px-6">
        <div class="flex flex-col lg:flex-row items-center">
          <div
            class="w-full lg:w-3/5 relative mb-16 lg:mb-0"
            :class="{ 'opacity-100 translate-x-0': isDashboardInView, 'opacity-0 -translate-x-8': !isDashboardInView }"
          >
            <div class="bg-white rounded-3xl shadow-xl overflow-hidden">
              <div class="bg-gradient-to-r from-blue-500 to-indigo-500 p-6">
                <div class="flex justify-between items-center mb-8">
                  <div>
                    <h3 class="text-white text-lg font-medium">Credit Dashboard</h3>
                    <p class="text-white opacity-80">Welcome back, John!</p>
                  </div>
                  <div class="h-10 w-10 rounded-full bg-white/20 flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
                  </div>
                </div>
                <div class="grid grid-cols-3 gap-4">
                  <div class="bg-white/10 rounded-xl p-4">
                    <p class="text-white text-xs mb-1 opacity-80">Total Balance</p>
                    <p class="text-white text-xl font-medium">$3,250.45</p>
                  </div>
              
                  <div class="bg-white/10 rounded-xl p-4">
                    <p class="text-white text-xs mb-1 opacity-80">Available Credit</p>
                    <p class="text-white text-xl font-medium">$7,500.00</p>
                  </div>

                  <div class="bg-white/10 rounded-xl p-4">
                    <p class="text-white text-xs mb-1 opacity-80">Due Date</p>
                    <p class="text-white text-xl font-medium">Apr 15</p>
                  </div>

                </div>

              </div>
              <div class="p-6 border-b border-gray-100">
                <div class="flex justify-between items-center mb-2">
                  <h4 class="text-gray-800 font-medium">Credit Limit</h4>
                  <p class="text-blue-500 font-medium">$10,000</p>
                </div>  

                <div class="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                  <div class="h-full bg-blue-500 rounded-full" style="width: 32.5%;"></div>
                </div>
                <div class="flex justify-between mt-2">
                  <p class="text-xs text-gray-500">$0</p>
                  <p class="text-xs text-gray-500">$10,000</p>
                </div>
              </div>
              <div class="p-6">
                <h4 class="text-gray-800 font-medium mb-4">Recent Transactions</h4>
                <div class="space-y-4">
                  <div v-for="(transaction, index) in transactions" :key="index" class="flex items-center justify-between">
                    <div class="flex items-center">
                      <div class="w-10 h-10 rounded-full flex items-center justify-center" :class="{
                        'bg-blue-100 text-blue-600': transaction.icon === 'shopping',
                        'bg-red-100 text-red-600': transaction.icon === 'entertainment',
                        'bg-green-100 text-green-600': transaction.icon === 'food',
                        'bg-yellow-100 text-yellow-600': transaction.icon === 'transport',
                      }">
                        <svg v-if="transaction.icon === 'shopping'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
                        </svg>
                        <svg v-else-if="transaction.icon === 'entertainment'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
                        </svg>
                        <svg v-else-if="transaction.icon === 'food'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                        </svg>
                        <svg v-else-if="transaction.icon === 'transport'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"></path>
                        </svg>
                      </div>
                      <div class="ml-3">
                        <p class="text-sm font-medium text-gray-800">{{ transaction.name }}</p>
                        <p class="text-xs text-gray-500">{{ transaction.category }}</p>
                      </div>
                    </div>
                    <div class="text-right">
                      <p class="text-sm font-medium text-gray-800">{{ transaction.amount.toFixed(2) }}</p>
                      <p class="text-xs text-gray-500">{{ transaction.date }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="absolute -bottom-10 -left-10 w-64 h-64 rounded-full bg-blue-100 opacity-70 blur-3xl -z-10"></div>
          </div>
          <div
            class="w-full lg:w-2/5 lg:pl-16"
            :class="{ 'opacity-100 translate-x-0': isDashboardInView, 'opacity-0 translate-x-8': !isDashboardInView }"
          >
            <h2 class="text-3xl md:text-4xl font-bold mb-6">
              Your Entire Credit Life in One Dashboard
            </h2>
            <p class="text-xl text-gray-600 mb-8">
              Track spending, manage limits, and make payments all from one intuitive interface.
            </p>
            <div class="space-y-6">
              <div class="flex">
                <div class="flex-shrink-0 mt-1">
                  <div class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-100 text-blue-600">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Instant Apply</h3>
                  <p class="mt-1 text-gray-600">Seamless application process with instant credit card approval for qualified users.</p>
                </div>
              </div>
              <div class="flex">
                <div class="flex-shrink-0 mt-1">
                  <div class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-100 text-blue-600">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Flexible Limits</h3>
                  <p class="mt-1 text-gray-600">Adjust your credit limits as needed and monitor your utilization effortlessly.</p>
                </div>
              </div>
              <div class="flex">
                <div class="flex-shrink-0 mt-1">
                  <div class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-100 text-blue-600">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Smart Analytics</h3>
                  <p class="mt-1 text-gray-600">Get insights on your spending patterns and recommendations to improve your credit.</p>
                </div>
              </div>
            </div>
            <div class="mt-10">
              <button @click="router.push('/register')" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                Explore the Dashboard
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section id="pricing" class="py-20" ref="ctaRef">
      <div class="container mx-auto px-6">
        <div
          class="bg-gradient-to-r from-blue-500 to-indigo-500 rounded-3xl overflow-hidden"
          :class="{ 'opacity-100 scale-100': isCtaInView, 'opacity-0 scale-95': !isCtaInView }"
        >
          <div class="px-6 py-20 md:px-20 flex flex-col md:flex-row items-center justify-between">
            <div class="text-center md:text-left mb-10 md:mb-0">
              <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">
                Ready to Simplify Your Credit Life?
              </h2>
              <p class="text-xl text-white text-opacity-90 max-w-2xl">
                Join thousands of users who have transformed how they manage their credit cards with our intuitive platform.
              </p>
            </div>
            <div class="flex flex-col space-y-4 md:flex-row md:space-y-0 md:space-x-4">
              <button @click="scrollToSection('features')" class="px-6 py-3 bg-white text-blue-600 rounded-lg hover:bg-gray-100 transition-colors">
                Learn More
              </button>
              <button @click="router.push('/register')" class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 border border-white border-opacity-20 transition-colors">
                Get Started Now →
              </button>
            </div>
          </div>
          <div class="absolute top-0 right-0 -mt-20 -mr-20 w-64 h-64 rounded-full bg-indigo-400 opacity-20 blur-3xl"></div>
          <div class="absolute bottom-0 left-0 -mb-32 -ml-32 w-96 h-96 rounded-full bg-blue-400 opacity-20 blur-3xl"></div>
        </div>
      </div>
    </section>

    <ChatWidget/>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import ChatWidget from '../components/ChatWidget.vue';

// Router for navigation
const router = useRouter();

// Navigation
const scrolled = ref(false);
const mobileMenuOpen = ref(false);

const handleScroll = () => {
  scrolled.value = window.scrollY > 50;
};

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

// Smooth scrolling to sections
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

// Scroll to top for Credify logo
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// Hero Section
const isHeroLoaded = ref(false);

// Features Section
const features = ref([
  { title: "Apply Now", description: "Seamless application process with instant approval for qualified users.", icon: "application", color: "from-blue-500 to-blue-600" },
  { title: "Manage Limit", description: "Adjust your credit limits with a simple slider based on your needs.", icon: "limit", color: "from-indigo-500 to-indigo-600" },
  { title: "Secure Pay", description: "Secure transactions to the intended users.", icon: "secure", color: "from-purple-500 to-purple-600" },
  { title: "Track Spending", description: "Advanced analytics to track and categorize all your expenses.", icon: "track", color: "from-blue-500 to-indigo-500" },
]);
const isFeaturesInView = ref(false);
const featuresRef = ref(null);

// Dashboard Section
const isDashboardInView = ref(false);
const dashboardRef = ref(null);
const transactions = ref([
  { name: 'Amazon', date: 'Today', amount: -84.99, category: 'Shopping', icon: 'shopping' },
  { name: 'Netflix', date: 'Yesterday', amount: -14.99, category: 'Entertainment', icon: 'entertainment' },
  { name: 'Starbucks', date: 'Mar 27', amount: -5.75, category: 'Food & Drinks', icon: 'food' },
  { name: 'Uber', date: 'Mar 26', amount: -23.50, category: 'Transportation', icon: 'transport' },
]);

// CTA Section
const isCtaInView = ref(false);
const ctaRef = ref(null);

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  setTimeout(() => {
    isHeroLoaded.value = true;
  }, 300);

  const featuresObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) isFeaturesInView.value = true;
  }, { threshold: 0.1 });
  if (featuresRef.value) featuresObserver.observe(featuresRef.value);

  const dashboardObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) isDashboardInView.value = true;
  }, { threshold: 0.1 });
  if (dashboardRef.value) dashboardObserver.observe(dashboardRef.value);

  const ctaObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) isCtaInView.value = true;
  }, { threshold: 0.1 });
  if (ctaRef.value) ctaObserver.observe(ctaRef.value);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.animate-float {
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

section > div > div > div {
  transition: all 0.8s ease-out;
}

section#dashboard > div > div > div:last-child {
  transition: all 0.8s ease-out 0.3s;
}

section#pricing > div > div {
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>