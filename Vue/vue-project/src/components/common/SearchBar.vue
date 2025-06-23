<template>
  <div class="search-bar">
    <input 
      type="text" 
      v-model="searchQuery" 
      :placeholder="placeholder" 
      @input="handleInput"
      @keyup.enter="handleSearch"
    />
    <button 
      class="search-button" 
      @click="handleSearch"
      aria-label="Search"
    >
      🔍
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  initialQuery: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Search...'
  },
  debounceTime: {
    type: Number,
    default: 300
  }
});

const emit = defineEmits(['search', 'input']);

const searchQuery = ref(props.initialQuery);
let debounceTimeout = null;

function handleInput() {
  // Clear previous timeout
  if (debounceTimeout) {
    clearTimeout(debounceTimeout);
  }
  
  // Set new timeout
  debounceTimeout = setTimeout(() => {
    emit('input', searchQuery.value);
  }, props.debounceTime);
}

function handleSearch() {
  // Clear any pending debounce
  if (debounceTimeout) {
    clearTimeout(debounceTimeout);
    debounceTimeout = null;
  }
  
  emit('search', searchQuery.value);
}

// Watch for changes in initialQuery prop
watch(() => props.initialQuery, (newValue) => {
  searchQuery.value = newValue;
});
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  background-color: white;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  overflow: hidden;
  transition: box-shadow 0.2s ease;
}

.search-bar:focus-within {
  box-shadow: 0 0 0 2px rgba(0, 91, 172, 0.2);
  border-color: var(--primary-color);
}

.search-bar input {
  flex: 1;
  padding: 10px 15px;
  border: none;
  font-size: 1rem;
  outline: none;
}

.search-button {
  background: none;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-button:hover {
  background-color: var(--background-light);
}
</style>
