<template>
  <div class="destination-detail-page">
    <div class="page-header">
      <BackButton />
      <h1>Destination Detail</h1>
    </div>
    
    <div class="detail-content">
      <p class="placeholder-text">This is a placeholder for the Destination Detail page. In a complete implementation, this would show detailed information about a specific destination.</p>
    </div>
  </div>
</template>

<script setup>
import BackButton from '../components/common/BackButton.vue';
</script>

<style scoped>
.destination-detail-page {
  padding: 20px;
  height: calc(100vh - var(--topbar-height));
  overflow-y: auto;
  background-color: rgba(255, 255, 255, 0.9);
  position: relative;
  z-index: 2;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin-left: 15px;
  color: var(--primary-color);
}

.detail-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.placeholder-text {
  color: var(--text-light);
  font-style: italic;
  text-align: center;
  margin-top: 100px;
}
</style>
