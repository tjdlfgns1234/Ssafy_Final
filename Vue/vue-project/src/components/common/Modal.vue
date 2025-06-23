<template>
  <Teleport to="body">
    <div v-if="modelValue" class="modal-overlay" @click="closeOnOverlayClick ? $emit('update:modelValue', false) : null">
      <div class="modal-container" :class="[size]" @click.stop>
        <div class="modal-header">
          <slot name="header">
            <h2>{{ title }}</h2>
            <button 
              v-if="showCloseButton" 
              class="close-button" 
              @click="$emit('update:modelValue', false)" 
              aria-label="Close"
            >
              ✕
            </button>
          </slot>
        </div>
        
        <div class="modal-body">
          <slot></slot>
        </div>
        
        <div v-if="$slots.footer" class="modal-footer">
          <slot name="footer"></slot>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { onMounted, onBeforeUnmount, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large', 'full'].includes(value)
  },
  showCloseButton: {
    type: Boolean,
    default: true
  },
  closeOnOverlayClick: {
    type: Boolean,
    default: true
  },
  closeOnEsc: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'close']);

function handleEscKey(event) {
  if (event.key === 'Escape' && props.closeOnEsc && props.modelValue) {
    emit('update:modelValue', false);
  }
}

// Prevent scrolling on the body when modal is open
function preventBodyScroll() {
  document.body.style.overflow = 'hidden';
}

function restoreBodyScroll() {
  document.body.style.overflow = '';
}

onMounted(() => {
  document.addEventListener('keydown', handleEscKey);
  if (props.modelValue) {
    preventBodyScroll();
  }
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleEscKey);
  restoreBodyScroll();
});

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    preventBodyScroll();
  } else {
    restoreBodyScroll();
    emit('close');
  }
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fade-in 0.2s ease;
}

.modal-container {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  animation: modal-appear 0.3s ease;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-container.small {
  width: 90%;
  max-width: 400px;
}

.modal-container.medium {
  width: 90%;
  max-width: 600px;
}

.modal-container.large {
  width: 90%;
  max-width: 800px;
}

.modal-container.full {
  width: 95%;
  max-width: 1200px;
  height: 90vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--primary-color);
}

.close-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--text-light);
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .modal-container.small,
  .modal-container.medium,
  .modal-container.large {
    width: 95%;
  }
}
</style>
