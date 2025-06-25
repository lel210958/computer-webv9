<template>
  <div class="breadcrumb">
    <span 
      class="breadcrumb-item" 
      @click="navigateToPath('')"
    >
      根目录
    </span>
    <template v-if="pathSegments.length > 0">
      <span 
        v-for="(segment, index) in pathSegments" 
        :key="index"
        class="breadcrumb-separator"
      >
        /
      </span>
      <span 
        v-for="(segment, index) in pathSegments" 
        :key="`path-${index}`"
        class="breadcrumb-item" 
        @click="navigateToPath(getFullPath(index))"
      >
        {{ segment }}
      </span>
    </template>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'Breadcrumb',
  props: {
    currentPath: {
      type: String,
      default: ''
    }
  },
  emits: ['path-change'],
  setup(props, { emit }) {
    const pathSegments = computed(() => {
      if (!props.currentPath) return []
      return props.currentPath.split('/').filter(segment => segment)
    })

    const navigateToPath = (path) => {
      emit('path-change', path)
    }

    const getFullPath = (index) => {
      const segments = pathSegments.value.slice(0, index + 1)
      return segments.join('/')
    }

    return {
      pathSegments,
      navigateToPath,
      getFullPath
    }
  }
}
</script>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 18px;
  background: #f7fafd;
  border-radius: 10px;
  font-size: 1em;
  box-shadow: 0 1px 6px rgba(30, 80, 200, 0.06);
  border: 1px solid #e3e8f0;
}

.breadcrumb-item {
  color: #1976d2;
  cursor: pointer;
  margin-right: 5px;
  padding: 2px 10px;
  border-radius: 6px;
  transition: background 0.15s, color 0.15s;
  font-weight: 500;
}

.breadcrumb-item:hover {
  background: #e3f2fd;
  color: #0d47a1;
}

.breadcrumb-separator {
  color: #8ca0c8;
  margin: 0 2px;
  font-size: 1.1em;
  user-select: none;
}
</style> 