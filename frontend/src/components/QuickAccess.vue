<template>
  <div class="quick-access">
    <h3>快速访问</h3>
    <div class="quick-items">
      <div
        v-for="item in quickItems"
        :key="item.type"
        class="quick-item"
        @click="handleQuickAccess(item.type)"
      >
        <div class="quick-icon">
          <i :class="item.icon"></i>
        </div>
        <div class="quick-name">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'QuickAccess',
  setup() {
    const router = useRouter()
    
    const quickItems = [
      { type: 'doc', name: '文档', icon: 'bi bi-file-earmark-text-fill' },
      { type: 'image', name: '图片', icon: 'bi bi-image-fill' },
      { type: 'video', name: '视频', icon: 'bi bi-camera-video-fill' },
      { type: 'music', name: '音乐', icon: 'bi bi-music-note-beamed' },
      { type: 'zip', name: '压缩包', icon: 'bi bi-file-earmark-zip-fill' }
    ]
    
    const handleQuickAccess = (type) => {
      router.push({
        path: '/category',
        query: { type }
      })
    }
    
    return {
      quickItems,
      handleQuickAccess
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.quick-access {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
  margin-bottom: 20px;
}

.quick-access h3 {
  color: #1976d2;
  font-size: 1.1em;
  margin-bottom: 16px;
  font-weight: 600;
}

.quick-items {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: background 0.15s;
  min-width: 80px;
}

.quick-item:hover {
  background: #e3f2fd;
}

.quick-icon {
  font-size: 2em;
  color: #1976d2;
}

.quick-name {
  font-size: 0.9em;
  color: #333;
  font-weight: 500;
  text-align: center;
}
</style> 