<template>
  <div class="card" :class="{ 'card-hover': hover }">
    <div v-if="$slots.image" class="card-image">
      <slot name="image"></slot>
    </div>
    <div v-if="$slots.header" class="card-header">
      <slot name="header"></slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  hover: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-image {
  width: 100%;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  display: block;
}

.card-header {
  padding: 15px;
  border-bottom: 1px solid var(--border-color);
}

.card-body {
  padding: 15px;
}

.card-footer {
  padding: 15px;
  border-top: 1px solid var(--border-color);
}
</style>
