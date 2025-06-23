<template>
  <div 
    v-if="visible" 
    class="alert" 
    :class="[`alert-${type}`, { 'alert-dismissible': dismissible }]"
    role="alert"
  >
    <div class="alert-content">
      <div v-if="title" class="alert-title">{{ title }}</div>
      <div class="alert-message">
        <slot></slot>
      </div>
    </div>
    <button 
      v-if="dismissible" 
      class="alert-close" 
      @click="dismiss"
      aria-label="Close"
    >
      ✕
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['info', 'success', 'warning', 'error'].includes(value)
  },
  title: {
    type: String,
    default: ''
  },
  dismissible: {
    type: Boolean,
    default: false
  },
  autoClose: {
    type: Boolean,
    default: false
  },
  duration: {
    type: Number,
    default: 5000
  }
});

const emit = defineEmits(['close']);

const visible = ref(true);

function dismiss() {
  visible.value = false;
  emit('close');
}

onMounted(() => {
  if (props.autoClose) {
    setTimeout(() => {
      dismiss();
    }, props.duration);
  }
});
</script>

<style scoped>
.alert {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 16px;
  animation: alert-appear 0.3s ease;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.alert-info {
  background-color: #e3f2fd;
  border-left: 4px solid #2196f3;
  color: #0c63e4;
}

.alert-success {
  background-color: #e8f5e9;
  border-left: 4px solid #4caf50;
  color: #2e7d32;
}

.alert-warning {
  background-color: #fff8e1;
  border-left: 4px solid #ff9800;
  color: #e65100;
}

.alert-error {
  background-color: #ffebee;
  border-left: 4px solid #f44336;
  color: #c62828;
}

.alert-close {
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  padding: 0 0 0 16px;
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.alert-close:hover {
  opacity: 1;
}

@keyframes alert-appear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
