<template>
  <button 
    class="detail-sidebar-toggle"
    @click="toggleSidebar"
    :class="{ 'collapsed': !isOpen }"
  >
    <span class="toggle-icon">▶</span>
  </button>
</template>

<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['toggle']);

function toggleSidebar() {
  emit('toggle');
}
</script>

<style scoped>
.detail-sidebar-toggle {
  position: absolute;
  top: 50%;
  left: -16px;
  transform: translateY(-50%);
  width: 16px;
  height: 40px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px 0 0 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.toggle-icon {
  font-size: 10px;
  transition: transform 0.3s ease;
}

.detail-sidebar-toggle.collapsed .toggle-icon {
  transform: rotate(180deg);
}
</style>
