<template>
    <div>
      <div
        v-if="showHelpTooltip && !isChatOpen"
        class="fixed bottom-20 right-20 bg-white text-black px-3 py-2 rounded-lg shadow-lg z-[9999] animate-bounce border"
      >
        👋 Need help?
      </div>
  
      <iframe
        v-show="isChatOpen"
        src="https://www.chatbase.co/chatbot-iframe/prUdnWrJbRhEwrxDmv_an"
        class="fixed bottom-24 right-5 w-[350px] h-[500px] rounded-xl shadow-xl z-[9999] border-none"
      ></iframe>
  
      <button
        @click="toggleChat"
        class="fixed bottom-5 right-5 w-14 h-14 text-2xl bg-blue-600 text-white rounded-full shadow-xl z-[10000] hover:bg-blue-700 transition-all duration-300"
      >
        💬
      </button>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  const isChatOpen = ref(false);
  const showHelpTooltip = ref(false);
  
  const toggleChat = () => {
    isChatOpen.value = !isChatOpen.value;
    showHelpTooltip.value = false; // hide tooltip once opened
  };
  
  onMounted(() => {
    // Show tooltip after a short delay (e.g., 3 seconds)
    setTimeout(() => {
      showHelpTooltip.value = true;
    }, 3000);
  });
  </script>
  