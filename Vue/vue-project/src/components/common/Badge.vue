<template>
  <span class="badge" :class="[`badge-${variant}`, { 'badge-outline': outline }]">
    <slot></slot>
  </span>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'success', 'danger', 'warning', 'info'].includes(value)
  },
  outline: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.badge {
  display: inline-block;
  padding: 0.25em 0.6em;
  font-size: 0.75rem;
  font-weight: bold;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 10px;
}

.badge-primary {
  background-color: var(--primary-color);
  color: white;
}

.badge-secondary {
  background-color: var(--secondary-color);
  color: white;
}

.badge-success {
  background-color: #28a745;
  color: white;
}

.badge-danger {
  background-color: #dc3545;
  color: white;
}

.badge-warning {
  background-color: #ffc107;
  color: #212529;
}

.badge-info {
  background-color: #17a2b8;
  color: white;
}

.badge-outline {
  background-color: transparent;
}

.badge-outline.badge-primary {
  color: var(--primary-color);
  border: 1px solid var(--primary-color);
}

.badge-outline.badge-secondary {
  color: var(--secondary-color);
  border: 1px solid var(--secondary-color);
}

.badge-outline.badge-success {
  color: #28a745;
  border: 1px solid #28a745;
}

.badge-outline.badge-danger {
  color: #dc3545;
  border: 1px solid #dc3545;
}

.badge-outline.badge-warning {
  color: #ffc107;
  border: 1px solid #ffc107;
}

.badge-outline.badge-info {
  color: #17a2b8;
  border: 1px solid #17a2b8;
}
</style>
