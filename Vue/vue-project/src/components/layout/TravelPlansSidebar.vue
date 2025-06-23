<template>
<div 
  class="travel-plans-sidebar" 
  :class="{ 'collapsed': !isOpen }"
  :style="{ 
    left: mainSidebarIsOpen ? `${mainSidebarWidth}px` : '60px',
    width: isOpen ? `${width}px` : '0',
    zIndex: 'var(--z-index-travel-sidebar)',
    maxWidth: isOpen ? `calc(100vw - ${mainSidebarIsOpen ? mainSidebarWidth + 60 : 120}px)` : '0'
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
    @wheel="handleMouseWheel"
  ></div>
</div>

<!-- Toggle button for collapsed state - positioned below Google Maps controls -->
<button 
  v-if="!isOpen"
  class="open-sidebar-button"
  :style="{ 
    left: mainSidebarIsOpen ? `${mainSidebarWidth + 10}px` : '70px',
    top: '120px' // Position below Google Maps controls
  }"
  @click="toggleSidebar"
>
  <span>{{ title }}</span>
</button>
</template>

<script setup>
import { ref, computed, onMounted, watch, onBeforeUnmount } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useModalStore } from '../../stores/modal';

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
mainSidebarIsOpen: {
  type: Boolean,
  default: true
},
mainSidebarWidth: {
  type: Number,
  default: 250
}
});

const emit = defineEmits(['toggle', 'resize']);

const authStore = useAuthStore();
const modalStore = useModalStore();

const isResizing = ref(false);

// Resize methods
const onMouseMove = ref(null);
const onMouseUp = ref(null);

const startResize = (event) => {
  event.preventDefault();
  isResizing.value = true;

  const startX = event.clientX;
  const startWidth = props.width;

  const mouseMoveHandler = (mouseMoveEvent) => {
    if (!isResizing.value) return;
    const newWidth = startWidth + (mouseMoveEvent.clientX - startX);
    const minWidth = 200;
    const maxWidth = window.innerWidth - (props.mainSidebarIsOpen ? props.mainSidebarWidth + 60 : 120); // Allow almost full screen
    
    if (newWidth >= minWidth && newWidth <= maxWidth) {
      emit('resize', newWidth);
    }
  }

  const mouseUpHandler = () => {
    isResizing.value = false;
    document.removeEventListener('mousemove', mouseMoveHandler);
    document.removeEventListener('mouseup', mouseUpHandler);
  }

  onMouseMove.value = mouseMoveHandler;
  onMouseUp.value = mouseUpHandler;

  document.addEventListener('mousemove', mouseMoveHandler);
  document.addEventListener('mouseup', mouseUpHandler);
}

onBeforeUnmount(() => {
  if (onMouseMove.value) {
    document.removeEventListener('mousemove', onMouseMove.value);
  }
  if (onMouseUp.value) {
    document.removeEventListener('mouseup', onMouseUp.value);
  }
});

const handleMouseWheel = (event) => {
  event.preventDefault();

  // Determine direction and amount to resize
  const delta = event.deltaY > 0 ? -10 : 10;
  const newWidth = props.width + delta;
  const minWidth = 200;
  const maxWidth = window.innerWidth - (props.mainSidebarIsOpen ? props.mainSidebarWidth + 60 : 120);

  if (newWidth >= minWidth && newWidth <= maxWidth) {
    emit('resize', newWidth);
  }
}

function toggleSidebar() {
emit('toggle');
}

// Watch for changes in isOpen prop
watch(() => props.isOpen, (newIsOpen) => {
if (!newIsOpen) {
  // When sidebar is closed, we might want to reset some state
  // or perform other actions
}
});
</script>

<style scoped>
.travel-plans-sidebar {
position: fixed;
top: var(--topbar-height);
bottom: 0;
background-color: white;
box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
display: flex;
flex-direction: column;
transition: left 0.3s ease, width 0.3s ease;
overflow: hidden;
z-index: 200; /* Ensure this is higher than the map z-index */
}

.travel-plans-sidebar.collapsed {
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
right: 0;
width: 5px;
height: 100%;
cursor: ew-resize;
background-color: transparent;
}

.resize-handle:hover {
background-color: rgba(0, 0, 0, 0.1);
}

.open-sidebar-button {
  position: fixed;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 12px;
  font-weight: bold;
  cursor: pointer;
  z-index: 199;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: left 0.3s ease, background-color 0.2s ease;
  font-size: 12px;
  white-space: nowrap;
}

.open-sidebar-button:hover {
background-color: #004c8e;
}

.toggle-icon {
font-size: 14px;
transform: rotate(0deg);
transition: transform 0.2s ease;
}

.toggle-text {
font-size: 12px;
letter-spacing: 1px;
}

.open-sidebar-button:hover .toggle-icon {
transform: rotate(10deg);
}

/* Hide toggle button when main sidebar is collapsed to avoid overlap */
/* @media (max-width: 768px) {
.open-sidebar-button {
  display: none;
}
} */
</style>
