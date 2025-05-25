<template>
  <div class="min-h-screen flex bg-gradient-to-br from-purple-50 to-blue-50">
    <!-- Left Column with Hero Content -->
    <div class="hidden lg:flex lg:w-1/2">
      <div 
        class="absolute inset-0 bg-cover bg-center"
        :style="{ backgroundImage: `url('https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=1470&auto=format&fit=crop')` }"
      >
        <div class="absolute inset-0 bg-gradient-to-r from-purple-900/80 to-blue-900/60"></div>
      </div>
      <div class="relative z-10 w-full h-full flex items-center justify-center px-4 sm:px-6 lg:px-8">
        <div class="text-center text-white">
          <!-- Logo -->
          <a href="/" class="flex justify-center items-center gap-3 mb-8">
  <div class="h-14 w-14 rounded-full bg-gradient-to-r from-blue-600 to-purple-600 flex items-center justify-center">
    <span class="text-white font-bold text-2xl">C</span>
  </div>
  <h1 class="text-5xl font-bold text-white">Credify</h1>
</a>
          <!-- Hero Text -->
          <h2 class="text-4xl md:text-6xl font-bold mb-6 leading-tight">
            Master Your Credit Card
            <br />
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-purple-300 to-blue-300">
              Management Portal
            </span>
          </h2>
          <p class="text-xl md:text-2xl mb-10 max-w-3xl mx-auto leading-relaxed">
            Take control of your financial future with our comprehensive credit card management platform. 
            Track spending, optimize rewards, and make smarter financial decisions.
          </p>
        </div>
      </div>
    </div>

    <!-- Right Column with Login/Forgot Password Form -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <router-link to="/" class="inline-flex items-center gap-2 mb-8">
            <div class="w-12 h-12 rounded-full bg-purple-gradient flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <rect width="20" height="14" x="2" y="5" rx="2" />
                <line x1="2" x2="22" y1="10" y2="10" />
              </svg>
            </div>
            <span class="font-bold text-3xl bg-clip-text text-transparent bg-gradient-to-r from-purple-600 to-blue-600">Credify</span>
          </router-link>
        </div>

        <!-- Login Form -->
        <div v-if="!showForgotPassword && !showResetPassword" class="w-full border-none bg-white/80 backdrop-blur-lg shadow-xl rounded-2xl p-6 animate-scale-in">
          <div class="space-y-1 text-center">
            <h2 class="text-2xl font-bold">Sign In</h2>
            <p class="text-gray-600">Enter your credentials to access your account</p>
          </div>

          <div class="space-y-4 mt-6">
            <div>
              <label class="block text-purple-800 mb-1">Email</label>
              <div class="relative">
                <svg class="absolute left-3 top-3 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12h2a4 4 0 0 0 0-8h-6a4 4 0 0 0-4 4v12a4 4 0 0 0 4 4h6a4 4 0 0 0 4-4v-4" />
                </svg>
                <input v-model="loginForm.email" type="email" placeholder="name@example.com"
                       class="w-full pl-10 p-2 border border-purple-100 rounded-md focus:border-purple-300 focus:ring focus:ring-purple-200 focus:ring-opacity-50" required />
              </div>
              <p v-if="errors.email" class="text-red-500 text-sm mt-1">{{ errors.email }}</p>
            </div>

            <div>
              <label class="block text-purple-800 mb-1">Password</label>
              <div class="relative">
                <svg class="absolute left-3 top-3 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-6-4c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-2-8h4m-4 8h4" />
                </svg>
                <input v-model="loginForm.password" :type="showPassword ? 'text' : 'password'" placeholder="••••••••"
                       class="w-full pl-10 pr-10 p-2 border border-purple-100 rounded-md focus:border-purple-300 focus:ring focus:ring-purple-200 focus:ring-opacity-50" required />
                <button @click="togglePassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                  <svg v-if="showPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.542 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.password" class="text-red-500 text-sm mt-1">{{ errors.password }}</p>
              <div class="text-right">
                <button @click="showForgotPassword = true" class="text-sm text-purple-600 hover:text-purple-800">Forgot password?</button>
              </div>
            </div>

            <div class="flex items-center space-x-2">
              <input type="checkbox" v-model="loginForm.rememberMe"
                     class="h-4 w-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500" />
              <label class="text-sm font-normal text-gray-700">Remember me</label>
            </div>

            <button @click.prevent="handleLogin" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-2 rounded-md hover:from-purple-700 hover:to-blue-700 transition-all duration-300">
              {{ isLoading ? 'Signing in...' : 'Sign in' }}
            </button>

            <div class="text-center mt-6">
              <span class="text-sm text-gray-600">
                Don't have an account?
                <router-link to="/register" class="text-purple-600 hover:text-purple-800 font-medium">Create an account</router-link>
              </span>
            </div>
          </div>
        </div>

        <!-- Forgot Password Form (Email Input) -->
        <div v-if="showForgotPassword && !showResetPassword" class="w-full border-none bg-white/80 backdrop-blur-lg shadow-xl rounded-2xl p-6 animate-scale-in">
          <div class="space-y-1 text-center">
            <h2 class="text-2xl font-bold">Reset Password</h2>
            <p class="text-gray-600">Enter your email to reset your password</p>
          </div>

          <div class="space-y-4 mt-6">
            <div>
              <label class="block text-purple-800 mb-1">Email</label>
              <div class="relative">
                <svg class="absolute left-3 top-3 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12h2a4 4 0 0 0 0-8h-6a4 4 0 0 0-4 4v12a4 4 0 0 0 4 4h6a4 4 0 0 0 4-4v-4" />
                </svg>
                <input v-model="forgotPasswordForm.email" type="email" placeholder="name@example.com"
                       class="w-full pl-10 p-2 border border-purple-100 rounded-md focus:border-purple-300 focus:ring focus:ring-purple-200 focus:ring-opacity-50" required />
              </div>
              <p v-if="errors.email" class="text-red-500 text-sm mt-1">{{ errors.email }}</p>
            </div>

            <button @click.prevent="handleForgotPassword" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-2 rounded-md hover:from-purple-700 hover:to-blue-700 transition-all duration-300">
              {{ isLoading ? 'Submitting...' : 'Submit' }}
            </button>

            <div class="text-center mt-6">
              <button @click="showForgotPassword = false" class="text-sm text-purple-600 hover:text-purple-800">Back to Sign In</button>
            </div>
          </div>
        </div>

        <!-- Reset Password Form (New Password Input) -->
        <div v-if="showResetPassword" class="w-full border-none bg-white/80 backdrop-blur-lg shadow-xl rounded-2xl p-6 animate-scale-in">
          <div class="space-y-1 text-center">
            <h2 class="text-2xl font-bold">Set New Password</h2>
            <p class="text-gray-600">Enter your new password</p>
          </div>

          <div class="space-y-4 mt-6">
            <div>
              <label class="block text-purple-800 mb-1">New Password</label>
              <div class="relative">
                <svg class="absolute left-3 top-3 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-6-4c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-2-8h4m-4 8h4" />
                </svg>
                <input v-model="resetPasswordForm.newPassword" :type="showNewPassword ? 'text' : 'password'" placeholder="••••••••"
                       class="w-full pl-10 pr-10 p-2 border border-purple-100 rounded-md focus:border-purple-300 focus:ring focus:ring-purple-200 focus:ring-opacity-50" required />
                <button @click="showNewPassword = !showNewPassword" class="absolute right-3 top-3">
                  <svg v-if="showNewPassword" class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.79m0 0L21 21" />
                  </svg>
                  <svg v-else class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.newPassword" class="text-red-500 text-sm mt-1">{{ errors.newPassword }}</p>
            </div>

            <div>
              <label class="block text-purple-800 mb-1">Confirm New Password</label>
              <div class="relative">
                <svg class="absolute left-3 top-3 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-6-4c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-2-8h4m-4 8h4" />
                </svg>
                <input v-model="resetPasswordForm.confirmPassword" :type="showConfirmPassword ? 'text' : 'password'" placeholder="••••••••"
                       class="w-full pl-10 pr-10 p-2 border border-purple-100 rounded-md focus:border-purple-300 focus:ring focus:ring-purple-200 focus:ring-opacity-50" required />
                <button @click="showConfirmPassword = !showConfirmPassword" class="absolute right-3 top-3">
                  <svg v-if="showConfirmPassword" class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.79m0 0L21 21" />
                  </svg>
                  <svg v-else class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.confirmPassword" class="text-red-500 text-sm mt-1">{{ errors.confirmPassword }}</p>
            </div>

            <button @click.prevent="handleResetPassword" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-2 rounded-md hover:from-purple-700 hover:to-blue-700 transition-all duration-300">
              {{ isLoading ? 'Resetting...' : 'Reset Password' }}
            </button>

            <div class="text-center mt-6">
              <button @click="showResetPassword = false; showForgotPassword = true" class="text-sm text-purple-600 hover:text-purple-800">Back to Email Input</button>
            </div>
          </div>
        </div>

        <!-- Success Message -->
        <div v-if="showSuccessMessage" class="w-full border-none bg-green-100 backdrop-blur-lg shadow-xl rounded-2xl p-6 animate-scale-in text-center">
          <h2 class="text-2xl font-bold text-green-800">Password Updated Successfully</h2>
          <button @click="showSuccessMessage = false; showForgotPassword = false; showResetPassword = false" 
                  class="mt-6 bg-gradient-to-r from-purple-600 to-blue-600 text-white py-2 px-4 rounded-md hover:from-purple-700 hover:to-blue-700 transition-all duration-300">
            Back to Sign In
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { CreditCard } from 'lucide-vue-next';

const router = useRouter();
const isLoading = ref(false);
const showForgotPassword = ref(false);
const showResetPassword = ref(false);
const showSuccessMessage = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
const showPassword = ref(false);
const userId = ref(null);

const loginForm = reactive({
  email: '',
  password: '',
  rememberMe: false,
});

const forgotPasswordForm = reactive({
  email: '',
});

const resetPasswordForm = reactive({
  newPassword: '',
  confirmPassword: '',
});

const errors = reactive({
  email: '',
  password: '',
  newPassword: '',
  confirmPassword: '',
});

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const validateLoginForm = () => {
  errors.email = '';
  errors.password = '';
  let isValid = true;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!loginForm.email) {
    errors.email = 'Email is required';
    isValid = false;
  } else if (!emailRegex.test(loginForm.email)) {
    errors.email = 'Email should be valid';
    isValid = false;
  }
  if (!loginForm.password) {
    errors.password = 'Password is required';
    isValid = false;
  }
  return isValid;
};

const validateForgotPasswordForm = () => {
  errors.email = '';
  let isValid = true;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!forgotPasswordForm.email) {
    errors.email = 'Email is required';
    isValid = false;
  } else if (!emailRegex.test(forgotPasswordForm.email)) {
    errors.email = 'Email should be valid';
    isValid = false;
  }
  return isValid;
};

const validateResetPasswordForm = () => {
  errors.newPassword = '';
  errors.confirmPassword = '';
  let isValid = true;
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$/;
  if (!resetPasswordForm.newPassword) {
    errors.newPassword = 'New password is required';
    isValid = false;
  } else if (!passwordRegex.test(resetPasswordForm.newPassword)) {
    errors.newPassword = 'Password must be at least 8 characters, include one uppercase, one lowercase, one digit, and one special character';
    isValid = false;
  }
  if (!resetPasswordForm.confirmPassword) {
    errors.confirmPassword = 'Confirm password is required';
    isValid = false;
  } else if (resetPasswordForm.newPassword !== resetPasswordForm.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
    isValid = false;
  }
  return isValid;
};

const fetchUserFullName = async (userId) => {
  try {
    const response = await axios.get('http://localhost:8089/api/profile');
    const users = response.data;
    const user = users.find(user => user.userId === parseInt(userId));
    return user ? user.fullName : null;
  } catch (error) {
    console.error('Error fetching user profile:', error);
    return null;
  }
};

const handleLogin = async () => {
  if (!validateLoginForm()) return;
  isLoading.value = true;

  try {
    const response = await axios.post('http://localhost:8089/api/profile/login', {
      email: loginForm.email,
      password: loginForm.password,
    });

    const storage = loginForm.rememberMe ? localStorage : sessionStorage;
    const userId = response.data.userId;
    let fullName = response.data.fullName;

    // If fullName is not in the login response, fetch it using userId
    if (!fullName) {
      fullName = await fetchUserFullName(userId);
      if (!fullName) {
        throw new Error('Unable to retrieve user name');
      }
    }

    // Store in storage
    storage.setItem('userId', userId);
    storage.setItem('fullName', fullName);

    router.push('/dashboard');
  } catch (error) {
    if (error.response) {
      const message = error.response.data || 'An error occurred';
      errors.email = message.includes('User') ? message : '';
      errors.password = message.includes('password') ? message : '';
    } else {
      errors.password = error.message || 'Failed to connect to the server';
    }
  } finally {
    isLoading.value = false;
  }
};

const handleForgotPassword = async () => {
  if (!validateForgotPasswordForm()) return;
  isLoading.value = true;

  try {
    const response = await axios.get('http://localhost:8089/api/profile');
    const users = response.data;
    const user = users.find(user => user.email === forgotPasswordForm.email);

    if (user) {
      userId.value = user.userId;
      showForgotPassword.value = false;
      showResetPassword.value = true;
    } else {
      errors.email = 'User with this email does not exist';
    }
  } catch (error) {
    console.error('Error fetching profiles:', error);
    errors.email = 'Failed to verify email. Please try again.';
  } finally {
    isLoading.value = false;
  }
};

const handleResetPassword = async () => {
  if (!validateResetPasswordForm()) return;
  isLoading.value = true;

  try {
    const response = await axios.put(`http://localhost:8089/api/profile/${userId.value}/password`, {
      password: resetPasswordForm.newPassword,
    });

    if (response.status === 200) {
      showResetPassword.value = false;
      showSuccessMessage.value = true;
      forgotPasswordForm.email = '';
      resetPasswordForm.newPassword = '';
      resetPasswordForm.confirmPassword = '';
    }
  } catch (error) {
    if (error.response) {
      const message = error.response.data || 'Failed to update password';
      errors.newPassword = message;
    } else {
      errors.newPassword = 'Failed to connect to the server';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.bg-purple-gradient {
  background: linear-gradient(to right, #6B46C1, #4299E1);
}
.animate-scale-in {
  animation: scaleIn 0.4s ease-in-out;
}
@keyframes scaleIn {
  0% {
    transform: scale(0.95);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>