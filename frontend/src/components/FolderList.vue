<template>
  <div class="folder-list">
    <div class="list-header">
      <div class="view-controls">
        <button :class="{active: viewMode === 'icon'}" @click="viewMode = 'icon'">图标视图</button>
        <button :class="{active: viewMode === 'list'}" @click="viewMode = 'list'">列表视图</button>
      </div>
      <div class="sort-controls">
        <span>排序:</span>
        <select v-model="sortField">
          <option value="name">名称</option>
          <option value="time">时间</option>
          <option value="size">大小</option>
        </select>
        <button class="sort-btn" @click="toggleSortOrder">
          <i class="bi" :class="sortOrder === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down'"></i>
        </button>
      </div>
    </div>
    
    <div v-if="loading" class="loading">正在加载...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="sortedFolders.length === 0" class="empty">当前目录为空</div>
    <div v-else>
      <!-- 图标视图 -->
      <div v-if="viewMode === 'icon'" class="icon-view">
        <div 
          v-for="folder in sortedFolders" 
          :key="folder.filePath" 
          class="icon-card" 
          @click="handleItemClick(folder)"
        >
          <div class="icon-img">
            <i :class="getFileIcon(folder)"></i>
          </div>
          <div class="icon-name">{{ folder.fileName }}</div>
        </div>
      </div>
      
      <!-- 列表视图 -->
      <table v-else class="list-view">
        <thead>
          <tr>
            <th></th>
            <th>名称</th>
            <th>类型</th>
            <th>大小</th>
            <th>修改时间</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="folder in sortedFolders" 
            :key="folder.filePath" 
            @click="handleItemClick(folder)"
            class="folder-row"
          >
            <td class="folder-icon">
              <i :class="getFileIcon(folder)"></i>
            </td>
            <td class="folder-name">{{ folder.fileName }}</td>
            <td class="folder-type">{{ folder.isDirectory == 1 ? '文件夹' : '文件' }}</td>
            <td class="folder-size">{{ folder.isDirectory == 1 ? '-' : formatFileSize(folder.fileSize) }}</td>
            <td class="folder-time">{{ formatTime(folder.lastModify) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'FolderList',
  props: {
    folders: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    error: {
      type: String,
      default: null
    }
  },
  emits: ['item-click'],
  setup(props, { emit }) {
    const viewMode = ref('icon')
    const sortField = ref('name')
    const sortOrder = ref('asc')
    
    const sortedFolders = computed(() => {
      const arr = [...props.folders]
      return arr.sort((a, b) => {
        let aValue, bValue
        switch (sortField.value) {
          case 'name':
            aValue = a.fileName?.toLowerCase() || ''
            bValue = b.fileName?.toLowerCase() || ''
            break
          case 'time':
            aValue = new Date(a.lastModify || 0).getTime()
            bValue = new Date(b.lastModify || 0).getTime()
            break
          case 'size':
            // 文件夹排前面
            if (a.isDirectory == 1 && b.isDirectory != 1) return -1
            if (a.isDirectory != 1 && b.isDirectory == 1) return 1
            aValue = a.isDirectory == 1 ? 0 : (parseInt(a.fileSize) || 0)
            bValue = b.isDirectory == 1 ? 0 : (parseInt(b.fileSize) || 0)
            break
          default:
            aValue = a.fileName?.toLowerCase() || ''
            bValue = b.fileName?.toLowerCase() || ''
        }
        if (sortOrder.value === 'asc') {
          return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
        } else {
          return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
        }
      })
    })
    
    const toggleSortOrder = () => {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    }
    
    const handleItemClick = (folder) => {
      emit('item-click', folder)
    }
    
    const getFileIcon = (folder) => {
      if (folder.isDirectory == 1) {
        return 'bi bi-folder-fill'
      }
      
      const iconMap = {
        'VIDEO': 'bi bi-camera-video-fill',
        'IMAGE': 'bi bi-image-fill',
        'DOCUMENT': 'bi bi-file-earmark-text-fill',
        'MUSIC': 'bi bi-music-note-beamed',
        'ZIP': 'bi bi-file-earmark-zip-fill'
      }
      return iconMap[folder.fileType] || 'bi bi-file-earmark-fill'
    }
    
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
    
    const formatTime = (time) => {
      if (!time) return '-'
      return new Date(time).toLocaleString('zh-CN')
    }
    
    return {
      viewMode,
      sortField,
      sortOrder,
      sortedFolders,
      toggleSortOrder,
      handleItemClick,
      getFileIcon,
      formatFileSize,
      formatTime
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.folder-list {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e3e8f0;
}

.view-controls {
  display: flex;
  gap: 10px;
}

.view-controls button {
  background: #fff;
  border: 1.5px solid #e3e8f0;
  color: #1976d2;
  border-radius: 8px;
  padding: 6px 18px;
  font-size: 1em;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border 0.15s;
}

.view-controls button.active,
.view-controls button:hover {
  background: #e3f2fd;
  color: #0d47a1;
  border-color: #1976d2;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-controls span {
  font-size: 0.98em;
  color: #1976d2;
  font-weight: 500;
}

.sort-controls select {
  padding: 6px 12px;
  border: 1px solid #dde3ec;
  border-radius: 6px;
  background: #fff;
  color: #333;
  font-size: 0.9em;
  cursor: pointer;
  transition: border-color 0.15s;
}

.sort-controls select:focus {
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

.icon-view {
  display: flex;
  flex-wrap: wrap;
  gap: 22px;
}

.icon-card {
  width: 110px;
  background: #f8fafc;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
  padding: 18px 8px 12px 8px;
  text-align: center;
  transition: box-shadow 0.18s, background 0.18s;
  cursor: pointer;
}

.icon-card:hover {
  background: #e3f2fd;
  box-shadow: 0 4px 16px rgba(30, 80, 200, 0.13);
}

.icon-img {
  font-size: 2.4em;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  color: #1976d2;
}

.icon-name {
  font-size: 1em;
  color: #222;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.list-view {
  width: 100%;
  border-collapse: collapse;
  font-size: 1em;
}

.list-view th,
.list-view td {
  padding: 10px 12px;
  border-bottom: 1px solid #e3e8f0;
  text-align: left;
}

.list-view th {
  background: #f3f6fb;
  color: #1976d2;
  font-weight: 600;
}

.folder-row {
  cursor: pointer;
  transition: background 0.15s;
}

.folder-row:hover {
  background: #f3f6fb;
}

.folder-icon {
  width: 36px;
  text-align: center;
  color: #1976d2;
}

.folder-name {
  font-weight: 500;
}

.folder-type {
  color: #666;
}

.folder-size {
  color: #666;
}

.folder-time {
  color: #666;
  font-size: 0.9em;
}

.loading,
.error,
.empty {
  text-align: center;
  color: #888;
  padding: 40px 0;
  font-size: 1.1em;
}

.error {
  color: #ff0000;
}
</style> 