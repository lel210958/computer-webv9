<template>
  <div class="category-stats">
    <div v-if="loading" class="loading">正在加载分类统计...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="categoryCount.length === 0" class="loading">暂无分类统计信息</div>
    <div v-else>
      <div
        v-for="category in categoryCount"
        :key="category.type"
        class="category-card"
      >
        <div class="category-icon">
          {{ getCategoryIcon(category.type) }}
        </div>
        <div class="category-name">{{ category.type }}</div>
        <div class="category-count">{{ category.count }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import { networkAPI } from '../api'

export default {
  name: 'CategoryStats',
  props: {
    networkLocation: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const categoryCount = ref([])
    const loading = ref(false)
    const error = ref(null)

    const loadCategoryStats = async () => {
      if (!props.networkLocation) {
        categoryCount.value = []
        return
      }

      try {
        loading.value = true
        error.value = null
        const data = await networkAPI.getCategoryCount(props.networkLocation)
        categoryCount.value = data.categoryCount || []
      } catch (err) {
        error.value = '加载分类统计失败'
        console.error('加载分类统计失败:', err)
      } finally {
        loading.value = false
      }
    }

    const getCategoryIcon = (type) => {
      const iconMap = {
        'video': '🎥',
        'image': '🖼️',
        'doc': '📄',
        'zip': '🗜️',
        'music': '🎵'
      }
      return iconMap[type] || '📁'
    }

    // 监听网络位置变化
    watch(() => props.networkLocation, () => {
      loadCategoryStats()
    }, { immediate: true })

    return {
      categoryCount,
      loading,
      error,
      getCategoryIcon
    }
  }
}
</script>

<style scoped>
.category-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 18px;
  margin-bottom: 30px;
}

.category-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(30, 80, 200, 0.07);
  padding: 18px 10px 14px 10px;
  text-align: center;
  transition: box-shadow 0.18s, background 0.18s;
  cursor: default;
  border: 1.5px solid #e3e8f0;
}
.category-card:hover {
  background: #f3fafd;
  box-shadow: 0 4px 18px rgba(30, 80, 200, 0.13);
}

.category-icon {
  font-size: 2.3em;
  margin-bottom: 8px;
  color: #1976d2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-name {
  font-size: 1em;
  margin-bottom: 4px;
  opacity: 0.92;
  color: #222;
  font-weight: 500;
}

.category-count {
  font-size: 1.4em;
  font-weight: bold;
  color: #0d47a1;
}

.loading, .error {
  text-align: center;
  padding: 30px 0 0 0;
  color: #888;
}
</style> 