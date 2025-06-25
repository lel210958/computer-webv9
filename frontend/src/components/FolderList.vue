<template>
  <div class="folder-list">
    <!-- 排序控制栏 -->
    <div class="sort-controls">
      <div class="sort-label">排序方式:</div>
      <select v-model="sortField" class="sort-select">
        <option value="name">名称</option>
        <option value="time">时间</option>
        <option value="size">大小</option>
      </select>
      <button 
        class="sort-btn" 
        @click="toggleSortOrder"
        :title="sortOrder === 'asc' ? '升序' : '降序'"
      >
        {{ sortOrder === 'asc' ? '↑' : '↓' }}
      </button>
    </div>

    <div v-if="loading" class="loading">正在加载文件夹列表...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="sortedFolderList.length === 0" class="loading">当前目录为空</div>
    <div v-else class="folder-grid">
      <div
        v-for="folder in sortedFolderList"
        :key="folder.filePath"
        class="folder-item"
        @click="handleFolderClick(folder)"
      >
        <div class="folder-icon">
          {{ getFolderIcon(folder) }}
        </div>
        <div class="folder-name">{{ folder.fileName }}</div>
        <div class="folder-info">
          {{ folder.isDirectory == 1 ? '文件夹' : `${formatFileSize(folder.fileSize)}` }}
        </div>
        <div class="folder-time" v-if="folder.lastModify">
          {{ formatTime(folder.lastModify) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch, computed } from 'vue'
import { networkAPI } from '../api'

export default {
  name: 'FolderList',
  props: {
    networkLocation: {
      type: Object,
      default: null
    },
    currentPath: {
      type: String,
      default: ''
    },
    filterType: {
      type: String,
      default: ''
    }
  },
  emits: ['folder-click'],
  setup(props, { emit }) {
    const folderListRaw = ref([])
    const loading = ref(false)
    const error = ref(null)
    
    // 排序相关状态
    const sortField = ref('name') // 排序字段：name, time, size
    const sortOrder = ref('asc')  // 排序方向：asc, desc

    const typeMap = {
      'doc': ['DOCUMENT'],
      'image': ['IMAGE'],
      'video': ['VIDEO'],
      'music': ['MUSIC'],
      'zip': ['ZIP']
    }

    // 计算排序后的文件夹列表
    const sortedFolderList = computed(() => {
      let filteredList = folderListRaw.value
      
      // 先按分类过滤
      if (props.filterType) {
        filteredList = filteredList.filter(item =>
          item.isDirectory === '1' ||
          (typeMap[props.filterType] && typeMap[props.filterType].includes(item.fileType))
        )
      }
      
      // 然后排序
      return filteredList.sort((a, b) => {
        let aValue, bValue
        
        switch (sortField.value) {
          case 'name':
            aValue = a.fileName.toLowerCase()
            bValue = b.fileName.toLowerCase()
            break
          case 'time':
            aValue = new Date(a.lastModify || 0).getTime()
            bValue = new Date(b.lastModify || 0).getTime()
            break
          case 'size':
            // 文件夹排在前面，然后按大小排序
            if (a.isDirectory === '1' && b.isDirectory !== '1') return -1
            if (a.isDirectory !== '1' && b.isDirectory === '1') return 1
            aValue = a.isDirectory === '1' ? 0 : (parseInt(a.fileSize) || 0)
            bValue = b.isDirectory === '1' ? 0 : (parseInt(b.fileSize) || 0)
            break
          default:
            aValue = a.fileName.toLowerCase()
            bValue = b.fileName.toLowerCase()
        }
        
        if (sortOrder.value === 'asc') {
          return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
        } else {
          return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
        }
      })
    })

    const filterFolders = () => {
      // 过滤逻辑已移到computed中
    }

    const loadFolderList = async () => {
      if (!props.networkLocation) {
        folderListRaw.value = []
        return
      }

      try {
        loading.value = true
        error.value = null
        const data = await networkAPI.getFolderList(props.networkLocation, props.currentPath)
        folderListRaw.value = data.folderList || []
      } catch (err) {
        error.value = '加载文件夹列表失败'
        console.error('加载文件夹列表失败:', err)
      } finally {
        loading.value = false
      }
    }

    const handleFolderClick = (folder) => {
      emit('folder-click', folder)
    }

    const getFolderIcon = (folder) => {
      if (folder.isDirectory == 1) {
        return '📂'
      }
      const iconMap = {
        'VIDEO': '🎥',
        'IMAGE': '🖼️',
        'DOCUMENT': '📄',
        'MUSIC': '🎵',
        'ZIP': '🗜️'
      }
      return iconMap[folder.fileType] || '📄'
    }

    // 切换排序方向
    const toggleSortOrder = () => {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    }

    // 格式化文件大小
    const formatFileSize = (size) => {
      if (!size || size === 0) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB', 'TB']
      let index = 0
      let fileSize = parseInt(size)
      while (fileSize >= 1024 && index < units.length - 1) {
        fileSize /= 1024
        index++
      }
      return `${fileSize.toFixed(1)} ${units[index]}`
    }

    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 监听网络位置、路径、分类变化
    watch([
      () => props.networkLocation,
      () => props.currentPath,
      () => props.filterType
    ], () => {
      loadFolderList()
    }, { immediate: true })

    return {
      folderListRaw,
      sortedFolderList,
      loading,
      error,
      sortField,
      sortOrder,
      handleFolderClick,
      getFolderIcon,
      toggleSortOrder,
      formatFileSize,
      formatTime
    }
  }
}
</script>

<style scoped>
.folder-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e3e8f0;
  box-shadow: 0 1px 4px rgba(30, 80, 200, 0.04);
}

.sort-label {
  font-size: 0.95em;
  color: #1976d2;
  font-weight: 500;
}

.sort-select {
  padding: 6px 12px;
  border: 1px solid #dde3ec;
  border-radius: 6px;
  background: #fff;
  color: #333;
  font-size: 0.9em;
  cursor: pointer;
  transition: border-color 0.15s;
}

.sort-select:focus {
  outline: none;
  border-color: #1976d2;
}

.sort-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #dde3ec;
  border-radius: 6px;
  background: #fff;
  color: #1976d2;
  font-size: 1.1em;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}

.sort-btn:hover {
  background: #e3f2fd;
  border-color: #1976d2;
  color: #0d47a1;
}

.folder-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 22px;
  margin-bottom: 30px;
  padding: 10px 0 10px 0;
}

.folder-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 8px 12px 8px;
  border-radius: 14px;
  cursor: pointer;
  transition: box-shadow 0.18s, background 0.18s, border 0.18s;
  border: 2px solid transparent;
  background: #fff;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.06);
  position: relative;
  min-height: 120px;
}

.folder-item:hover {
  background: #e3f2fd;
  border-color: #1976d2;
  box-shadow: 0 4px 16px rgba(30, 80, 200, 0.13);
}

.folder-icon {
  width: 54px;
  height: 54px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.3em;
  border-radius: 12px;
  background: #e3eefd;
  box-shadow: 0 1px 4px rgba(30, 80, 200, 0.04);
}

.folder-name {
  font-size: 1em;
  color: #222;
  text-align: center;
  word-break: break-word;
  margin-bottom: 4px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.folder-info {
  font-size: 0.88em;
  color: #6a7ba2;
  text-align: center;
  margin-bottom: 2px;
}

.folder-time {
  font-size: 0.8em;
  color: #8ca0c8;
  text-align: center;
}

.loading, .error {
  text-align: center;
  padding: 40px;
  color: #888;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sort-controls {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .sort-label {
    font-size: 0.9em;
  }
  
  .sort-select {
    font-size: 0.85em;
  }
}
</style> 