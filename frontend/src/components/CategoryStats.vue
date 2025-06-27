<template>
  <div class="category-stats">
    <h3>分类统计</h3>
    <div class="stats-grid">
      <div
        v-for="category in categoryStats"
        :key="category.type"
        class="stat-card"
        @click="handleCategoryClick(category.type)"
      >
        <div class="stat-icon">
          <i :class="getCategoryIcon(category.type)"></i>
        </div>
        <div class="stat-info">
          <div class="stat-name">{{ getCategoryName(category.type) }}</div>
          <div class="stat-count">{{ category.count }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'CategoryStats',
  props: {
    categoryStats: {
      type: Array,
      default: () => []
    }
  },
  setup() {
    const router = useRouter()
    
    const getCategoryIcon = (type) => {
      const iconMap = {
        'video': 'bi bi-camera-video-fill',
        'image': 'bi bi-image-fill',
        'doc': 'bi bi-file-earmark-text-fill',
        'zip': 'bi bi-file-earmark-zip-fill',
        'music': 'bi bi-music-note-beamed'
      }
      return iconMap[type] || 'bi bi-folder-fill'
    }
    
    const getCategoryName = (type) => {
      const nameMap = {
        'video': '视频',
        'image': '图片',
        'doc': '文档',
        'zip': '压缩包',
        'music': '音乐'
      }
      return nameMap[type] || type
    }
    
    const handleCategoryClick = (type) => {
      router.push({
        path: '/category',
        query: { type }
      })
    }
    
    return {
      getCategoryIcon,
      getCategoryName,
      handleCategoryClick
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.category-stats {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
  margin-bottom: 20px;
}

.category-stats h3 {
  color: #1976d2;
  font-size: 1.1em;
  margin-bottom: 16px;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s, transform 0.15s;
  border: 1px solid #e3e8f0;
}

.stat-card:hover {
  background: #e3f2fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 80, 200, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: #e3eefd;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8em;
  color: #1976d2;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-name {
  font-size: 0.95em;
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
}

.stat-count {
  font-size: 1.2em;
  color: #1976d2;
  font-weight: bold;
}
</style> 