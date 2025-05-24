<template>
    <div class="max-w-5xl mx-auto p-6 bg-white rounded-2xl shadow-md">
      <!-- Header -->
      <div class="bg-gradient-to-r from-purple-500 to-purple-600 p-6 rounded-t-2xl text-white text-left">
        <h2 class="text-3xl font-bold">Card Application</h2>
        <p class="text-sm mt-1">Step 1: Select your card type</p>
      </div>
  
      <!-- Card Selection -->
      <div class="p-6">
        <h3 class="text-xl font-semibold text-gray-800 mb-4 text-left">Choose Your Card</h3>
  
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div
            v-for="card in cards"
            :key="card.id"
            @click="selectCard(card)"
            :class="[
              'cursor-pointer rounded-xl overflow-hidden border transition hover:shadow-md',
              selectedCard?.id === card.id ? 'border-purple-500 ring-2 ring-purple-300' : 'border-gray-200',
            ]"
          >
            <!-- Card Design -->
            <div :class="card.bgClass" class="h-40 px-4 py-3 text-white rounded-t-xl relative">
              <div class="w-10 h-6 bg-yellow-300 rounded-sm mb-2"></div>
              <div class="text-lg font-mono tracking-widest mb-2">**** **** **** 1234</div>
              <div class="flex justify-between items-center text-sm">
                <div>
                  <div class="text-xs">VALID THRU</div>
                  <div>12/30</div>
                </div>
                <div class="uppercase font-bold tracking-wide text-lg">
                  {{ card.cardType }}
                </div>
              </div>
            </div>
  
            <!-- Card Details -->
            <div class="p-4 text-left">
              <h4 class="font-semibold text-gray-800">{{ card.title }}</h4>
              <p class="text-sm text-gray-500 mt-1">{{ card.description }}</p>
            </div>
          </div>
        </div>
  
        <!-- Credit Limit Input -->
        <div class="mt-8 text-left">
          <label class="block text-gray-700 font-medium mb-2">Requested Credit Limit</label>
          <input
            v-model="requestedLimit"
            type="number"
            placeholder="Enter your requested credit limit"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
        </div>
  
        <!-- Submit Button -->
        <div class="mt-10 flex justify-start">
          <button
            @click="submitApplication"
            class="bg-gradient-to-r from-purple-500 to-purple-600 text-white px-8 py-3 rounded-full text-lg font-semibold shadow-md hover:scale-105 transition transform duration-300"
          >
            Submit Application
          </button>
        </div>
  
        <!-- Success & Error Messages -->
        <div v-if="successMessage" class="mt-6 text-left text-green-600 font-bold text-lg">
          ✅ {{ successMessage }}
        </div>
  
        <div v-if="errorMessage" class="mt-6 text-left text-red-600 font-semibold text-md">
          ❌ {{ errorMessage }}
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import Navbar from './Navbar.vue'
  const cards = ref([
    {
      id: 1,
      cardType: 'VISA',
      title: 'Visa Signature',
      description: 'Global acceptance with premium benefits',
      bgClass: 'bg-blue-500',
    },
    {
      id: 2,
      cardType: 'MASTERCARD',
      title: 'World Mastercard',
      description: 'Exclusive rewards and travel benefits',
      bgClass: 'bg-gradient-to-r from-orange-500 to-red-500',
    },
    {
      id: 3,
      cardType: 'AMERICAN EXPRESS',
      title: 'American Express',
      description: 'Premium service and exclusive perks',
      bgClass: 'bg-gray-800',
    },
  ])
  
  const userId = ref(null)
  const selectedCard = ref(null)
  const requestedLimit = ref('')
  const successMessage = ref('')
  const errorMessage = ref('')
  
  // Retrieve userId from localStorage
  const getUserIdFromStorage = () => {
    try {
      const storedUserId = localStorage.getItem('userId')
      if (storedUserId) {
        userId.value = parseInt(storedUserId, 10)
        return true
      }
      return false
    } catch (err) {
      console.error('Error reading userId from localStorage:', err)
      return false
    }
  }
  
  onMounted(() => {
    if (!getUserIdFromStorage()) {
      errorMessage.value = 'User not found. Please login first.'
    }
  })
  
  const selectCard = (card) => {
    selectedCard.value = card
  }
  
  const submitApplication = async () => {
    successMessage.value = ''
    errorMessage.value = ''
  
    if (!userId.value) {
      errorMessage.value = 'User not found. Please login first.'
      return
    }
  
    if (!selectedCard.value || !requestedLimit.value) {
      errorMessage.value = 'Please select a card and enter your requested limit.'
      return
    }
  
    try {
      const payload = {
        user: { userId: userId.value },
        cardType: selectedCard.value.cardType,
        requestedLimit: parseFloat(requestedLimit.value),
      }
  
      const response = await axios.post('http://localhost:8089/cards/apply', payload)
  
      successMessage.value = `🎉 Your application for ${selectedCard.value.title} has been submitted successfully!`
      requestedLimit.value = ''
      selectedCard.value = null
  
      setTimeout(() => {
        successMessage.value = ''
      }, 4000)
    } catch (error) {
      console.error('Error submitting application:', error)
      errorMessage.value = 'Failed to submit application. Please try again later.'
    }
  }
  </script>
  
  <style scoped>
  input[type='number']::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }
  </style>