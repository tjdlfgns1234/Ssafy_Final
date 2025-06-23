<template>
  <div 
    class="resizable-sidebar" 
    :class="{ 'collapsed': !isOpen, [`position-${position}`]: true }"
    :style="{ 
      width: isOpen ? `${width}px` : '0',
      [position]: isOpen ? '0' : `-${width}px`,
      maxWidth: isOpen ? '100vw' : '0'
    }"
  >
    <div class="sidebar-header">
      <h2>{{ title }}</h2>
      <button class="close-button" @click="toggleSidebar">✕</button>
    </div>
    
    <div class="sidebar-content">
      <slot></slot>
    </div>
    
    <div 
      v-if="isOpen"
      class="resize-handle"
      @mousedown="startResize"
    ></div>
    
    <DetailSidebarToggle 
      v-if="!isOpen"
      :isOpen="isOpen"
      @toggle="toggleSidebar"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import DetailSidebarToggle from '../travel/DetailSidebarToggle.vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: true
  },
  width: {
    type: Number,
    default: 300
  },
  title: {
    type: String,
    default: '사이드바'
  },
  position: {
    type: String,
    default: 'right',
    validator: (value) => ['left', 'right'].includes(value)
  }
});

const emit = defineEmits(['toggle', 'resize']);

const isResizing = ref(false);

function toggleSidebar() {
  emit('toggle');
}

function startResize(event) {
  event.preventDefault();
  isResizing.value = true;

  const startX = event.clientX;
  const startWidth = props.width;
  
  const handleMouseMove = (e) => {
    if (!isResizing.value) return;
    
    let newWidth;
    if (props.position === 'right') {
      newWidth = startWidth + (startX - e.clientX);
    } else {
      newWidth = startWidth + (e.clientX - startX);
    }
    
    const minWidth = 250;
    const maxWidth = window.innerWidth - 100; // Allow almost full screen width
    
    if (newWidth >= minWidth && newWidth <= maxWidth) {
      emit('resize', newWidth);
    }
  };
  
  const handleMouseUp = () => {
    isResizing.value = false;
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  };
  
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
}
</script>

<style scoped>
.resizable-sidebar {
  position: fixed;
  top: var(--topbar-height);
  bottom: 0;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease, right 0.3s ease, left 0.3s ease;
  overflow: hidden;
  z-index: 200; /* Ensure this is higher than the map z-index */
  max-width: 100vw; /* Allow full viewport width */
}

.position-right {
  right: 0;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
}

.position-left {
  left: 0;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.resizable-sidebar.collapsed {
  box-shadow: none;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--primary-color);
  color: white;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.2rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.close-button {
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.resize-handle {
  position: absolute;
  top: 0;
  height: 100%;
  width: 5px;
  cursor: ew-resize;
  background-color: transparent;
}

.position-right .resize-handle {
  left: 0;
}

.position-left .resize-handle {
  right: 0;
}

.resize-handle:hover {
  background-color: rgba(0, 0, 0, 0.1);
}
</style>
