<template>
  <div class="pagination">
    <button 
      class="pagination-button" 
      :disabled="currentPage === 1"
      @click="changePage(currentPage - 1)"
      aria-label="Previous page"
    >
      ←
    </button>
    
    <div class="pagination-pages">
      <button 
        v-for="page in visiblePages" 
        :key="page"
        class="page-button"
        :class="{ 'active': page === currentPage }"
        @click="changePage(page)"
      >
        {{ page }}
      </button>
    </div>
    
    <button 
      class="pagination-button" 
      :disabled="currentPage === totalPages"
      @click="changePage(currentPage + 1)"
      aria-label="Next page"
    >
      →
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  maxVisiblePages: {
    type: Number,
    default: 5
  }
});

const emit = defineEmits(['page-change']);

const visiblePages = computed(() => {
  if (props.totalPages <= props.maxVisiblePages) {
    // If total pages is less than max visible, show all pages
    return Array.from({ length: props.totalPages }, (_, i) => i + 1);
  }
  
  // Calculate the range of pages to show
  const halfVisible = Math.floor(props.maxVisiblePages / 2);
  let startPage = Math.max(props.currentPage - halfVisible, 1);
  let endPage = Math.min(startPage + props.maxVisiblePages - 1, props.totalPages);
  
  // Adjust if we're near the end
  if (endPage === props.totalPages) {
    startPage = Math.max(endPage - props.maxVisiblePages + 1, 1);
  }
  
  return Array.from(
    { length: endPage - startPage + 1 },
    (_, i) => startPage + i
  );
});

function changePage(page) {
  if (page !== props.currentPage && page >= 1 && page <= props.totalPages) {
    emit('page-change', page);
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px 0;
}

.pagination-button {
  background: none;
  border: 1px solid var(--border-color);
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s ease;
}

.pagination-button:hover:not(:disabled) {
  background-color: var(--background-light);
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-pages {
  display: flex;
  margin: 0 10px;
}

.page-button {
  background: none;
  border: 1px solid var(--border-color);
  padding: 8px 12px;
  margin: 0 5px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.page-button:hover:not(.active) {
  background-color: var(--background-light);
}

.page-button.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
</style>
