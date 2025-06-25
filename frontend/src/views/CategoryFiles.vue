<template>
  <div class="category-files">
    <div class="nas-main">
      <!-- 右侧内容区 -->
      <div class="nas-content">
        <div v-if="!currentLocation" class="empty-tip">请选择左侧网络位置</div>
        <template v-else>
          <!-- 面包屑导航 -->
          <div class="nas-breadcrumb">
            <span class="breadcrumb-item back-btn" @click="goBack">← 返回</span>
            <span> / </span>
            <span class="breadcrumb-item category-breadcrumb">{{ getCategoryName(categoryType) }}</span>
          </div>
          
          <!-- 视图切换和排序控件 -->
          <div class="view-controls">
            <div class="view-switch">
              <button :class="{active: viewMode === 'icon'}" @click="viewMode = 'icon'">图标视图</button>
              <button :class="{active: viewMode === 'list'}" @click="viewMode = 'list'">列表视图</button>
            </div>
            <div class="sort-controls">
              <span class="sort-label">排序方式:</span>
              <select v-model="sortField" class="sort-select">
                <option value="name">名称</option>
                <option value="time">时间</option>
                <option value="size">大小</option>
              </select>
              <button class="sort-btn" @click="toggleSortOrder" :title="sortOrder === 'asc' ? '升序' : '降序'">
                {{ sortOrder === 'asc' ? '↑' : '↓' }}
              </button>
            </div>
          </div>

          <!-- 文件列表 -->
          <div v-if="loading" class="loading">正在加载{{ getCategoryName(categoryType) }}文件...</div>
          <div v-else-if="error" class="error">{{ error }}</div>
          <div v-else-if="sortedFiles.length === 0" class="empty">暂无{{ getCategoryName(categoryType) }}文件</div>
          <div v-else>
            <!-- 图标视图 -->
            <div v-if="viewMode === 'icon'" class="icon-view">
              <div 
                v-for="file in sortedFiles" 
                :key="file.filePath" 
                class="icon-card" 
                @click="handleFileClick(file)"
              >
                <div class="icon-img">{{ getFileIcon(file) }}</div>
                <div class="icon-name">{{ file.fileName }}</div>
                <div class="icon-size">{{ formatFileSize(file.fileSize) }}</div>
              </div>
            </div>

            <!-- 列表视图 -->
            <table v-else class="list-view">
              <thead>
                <tr>
                  <th></th>
                  <th>名称</th>
                  <th>大小</th>
                  <th>修改时间</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="file in sortedFiles" 
                  :key="file.filePath" 
                  @click="handleFileClick(file)"
                  class="file-row"
                >
                  <td class="file-icon">
                    <span>{{ getFileIcon(file) }}</span>
                  </td>
                  <td class="file-name">{{ file.fileName }}</td>
                  <td class="file-size">{{ formatFileSize(file.fileSize) }}</td>
                  <td class="file-time">{{ formatTime(file.lastModify) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
      </div>
    </div>

    <!-- 播放模态框 -->
    <div v-if="playUrl" class="play-modal">
      <div class="play-close-btn" @click="playUrl = ''">×</div>
      
      <!-- 左箭头 -->
      <div v-if="mediaFiles.length > 1" class="play-arrow play-left" @click="playMediaAt(playIndex - 1)">
        ‹
      </div>
      
      <!-- 右箭头 -->
      <div v-if="mediaFiles.length > 1" class="play-arrow play-right" @click="playMediaAt(playIndex + 1)">
        ›
      </div>
      
      <!-- 媒体内容 -->
      <video v-if="playType === 'VIDEO'" :src="playUrl" controls autoplay></video>
      <img v-else-if="playType === 'IMAGE'" :src="playUrl" />
      
      <!-- 播放信息 -->
      <div v-if="mediaFiles.length > 1" class="play-info">
        {{ playIndex + 1 }} / {{ mediaFiles.length }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { networkAPI } from '../api'

export default {
  name: 'CategoryFiles',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 从查询参数获取分类类型和网络位置
    const categoryType = ref(route.query.type || '')
    const locationParam = ref(route.query.location || '')
    
    // 网络位置相关
    const networkLocations = ref([])
    const loadingLocations = ref(true)
    const errorLocations = ref(null)
    const currentLocation = ref(null)
    
    // 文件列表相关
    const files = ref([])
    const loading = ref(false)
    const error = ref(null)
    
    // 视图和排序相关
    const viewMode = ref('icon')
    const sortField = ref('name')
    const sortOrder = ref('asc')
    
    // 播放相关
    const playUrl = ref('')
    const playType = ref('')
    const playIndex = ref(-1)
    
    // 计算排序后的文件列表
    const sortedFiles = computed(() => {
      return [...files.value].sort((a, b) => {
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
            aValue = parseInt(a.fileSize) || 0
            bValue = parseInt(b.fileSize) || 0
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
    
    // 获取媒体文件列表（用于播放）
    const mediaFiles = computed(() => {
      return sortedFiles.value.filter(file => 
        file.fileType === 'VIDEO' || file.fileType === 'IMAGE'
      )
    })
    
    // 加载网络位置
    const loadNetworkLocations = async () => {
      try {
        loadingLocations.value = true
        errorLocations.value = null
        const data = await networkAPI.getNetworkLocations()
        networkLocations.value = data.networkLocationList || []
        
        // 从查询参数解析网络位置信息
        if (locationParam.value) {
          try {
            currentLocation.value = JSON.parse(locationParam.value)
          } catch (err) {
            console.error('解析网络位置参数失败:', err)
            // 如果解析失败，默认选择第一个
            if (networkLocations.value.length > 0) {
              currentLocation.value = networkLocations.value[0]
            }
          }
        } else if (networkLocations.value.length > 0) {
          // 否则默认选择第一个
          currentLocation.value = networkLocations.value[0]
        }
      } catch (err) {
        errorLocations.value = '加载网络位置失败'
        console.error('加载网络位置失败:', err)
      } finally {
        loadingLocations.value = false
      }
    }
    
    // 加载分类文件
    const loadCategoryFiles = async () => {
      if (!currentLocation.value || !categoryType.value) {
        error.value = '缺少必要参数'
        return
      }
      
      try {
        loading.value = true
        error.value = null
        
        const data = await networkAPI.getCategoryFiles(currentLocation.value, categoryType.value)
        files.value = data.folderList || []
      } catch (err) {
        error.value = `加载${getCategoryName(categoryType.value)}文件失败`
        console.error('加载分类文件失败:', err)
      } finally {
        loading.value = false
      }
    }
    
    // 选中网络位置
    const selectLocation = (loc) => {
      // 点击网络位置时返回到nas-demo页面
      router.push('/nas-demo')
    }
    
    const isActive = (loc) => {
      return currentLocation.value && 
             currentLocation.value.ip === loc.ip && 
             currentLocation.value.name === loc.name
    }
    
    // 获取分类图标
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
    
    // 获取分类名称
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
    
    // 获取文件图标
    const getFileIcon = (file) => {
      const iconMap = {
        'VIDEO': '🎥',
        'IMAGE': '🖼️',
        'DOCUMENT': '📄',
        'MUSIC': '🎵',
        'ZIP': '🗜️'
      }
      return iconMap[file.fileType] || '📄'
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
    
    // 切换排序方向
    const toggleSortOrder = () => {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    }
    
    // 处理文件点击
    const handleFileClick = (file) => {
      if (file.fileType === 'VIDEO' || file.fileType === 'IMAGE') {
        // 处理视频和图片播放
        const filePathParam = encodeURIComponent(file.filePath)
        const playUrlStr = `/api/smc/api/network-location/stream?ip=${currentLocation.value.ip}&shareName=${encodeURIComponent(currentLocation.value.name)}&filePath=${filePathParam}`
        
        playUrl.value = playUrlStr
        playType.value = file.fileType
        playIndex.value = mediaFiles.value.findIndex(f => f.filePath === file.filePath)
      } else {
        alert(`文件: ${file.fileName}`)
      }
    }
    
    // 播放指定索引的媒体文件
    const playMediaAt = (idx) => {
      if (idx < 0 || idx >= mediaFiles.value.length) return
      
      const file = mediaFiles.value[idx]
      const filePathParam = encodeURIComponent(file.filePath)
      const playUrlStr = `/api/smc/api/network-location/stream?ip=${currentLocation.value.ip}&shareName=${encodeURIComponent(currentLocation.value.name)}&filePath=${filePathParam}`
      
      playUrl.value = playUrlStr
      playType.value = file.fileType
      playIndex.value = idx
    }
    
    // 返回上一页
    const goBack = () => {
      router.push('/nas-demo')
    }
    
    // 监听网络位置变化，重新加载分类文件
    watch(currentLocation, () => {
      if (currentLocation.value) {
        loadCategoryFiles()
      }
    })
    
    // 监听查询参数变化
    watch(() => route.query, (newQuery) => {
      categoryType.value = newQuery.type || ''
      locationParam.value = newQuery.location || ''
      loadNetworkLocations()
    }, { immediate: true })
    
    // 页面加载时获取数据
    onMounted(() => {
      loadNetworkLocations()
    })
    
    return {
      categoryType,
      networkLocations,
      loadingLocations,
      errorLocations,
      currentLocation,
      files,
      loading,
      error,
      viewMode,
      sortField,
      sortOrder,
      sortedFiles,
      mediaFiles,
      playUrl,
      playType,
      playIndex,
      selectLocation,
      isActive,
      getCategoryIcon,
      getCategoryName,
      getFileIcon,
      formatFileSize,
      formatTime,
      toggleSortOrder,
      handleFileClick,
      playMediaAt,
      goBack
    }
  }
}
</script>

<style scoped>
.category-files {
  height: 100vh;
  background: #f3f6fb;
  display: flex;
  flex-direction: column;
}

.nas-main {
  flex: 1;
  display: flex;
  min-height: 0;
}

.nas-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

.category-header {
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 18px;
  border: 1px solid #e3e8f0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #0d47a1;
  font-size: 1.1em;
}

.category-icon {
  font-size: 1.4em;
}

.nas-breadcrumb {
  margin-bottom: 18px;
  font-size: 1em;
}

.breadcrumb-item {
  color: #1976d2;
  cursor: pointer;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 6px;
  transition: background 0.15s;
}

.breadcrumb-item:hover {
  background: #e3f2fd;
}

.back-btn {
  color: #1976d2 !important;
  cursor: pointer;
  font-weight: 500;
  padding: 4px 8px !important;
  border-radius: 6px;
  transition: background 0.15s;
}

.back-btn:hover {
  background: #e3f2fd !important;
}

.category-breadcrumb {
  color: #0d47a1 !important;
  font-weight: 600;
  background: #e3f2fd;
  padding: 4px 12px !important;
  border-radius: 8px;
}

.view-controls {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.view-switch {
  display: flex;
  align-items: center;
  gap: 10px;
}

.view-switch button {
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

.view-switch button.active,
.view-switch button:hover {
  background: #e3f2fd;
  color: #0d47a1;
  border-color: #1976d2;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 0.98em;
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

.icon-view {
  display: flex;
  flex-wrap: wrap;
  gap: 22px;
}

.icon-card {
  width: 110px;
  background: #fff;
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
}

.icon-name {
  font-size: 1em;
  color: #222;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.icon-size {
  font-size: 0.85em;
  color: #6a7ba2;
}

.list-view {
  width: 100%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
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

.file-row {
  cursor: pointer;
  transition: background 0.15s;
}

.file-row:hover {
  background: #e3f2fd;
}

.file-icon {
  width: 40px;
  text-align: center;
  font-size: 1.2em;
}

.file-name {
  font-weight: 500;
  color: #222;
}

.file-size {
  color: #6a7ba2;
  font-size: 0.95em;
}

.file-time {
  color: #8ca0c8;
  font-size: 0.9em;
}

.loading,
.error,
.empty,
.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 1.1em;
}

.error {
  color: #f44336;
}

.play-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.play-modal video,
.play-modal img {
  max-width: 100vw;
  max-height: 100vh;
  width: auto;
  height: auto;
  display: block;
  margin: auto;
  object-fit: contain;
}

.play-close-btn {
  position: absolute;
  top: 24px;
  right: 36px;
  font-size: 2em;
  color: rgba(255, 255, 255, 0.55);
  background: transparent;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  border: none;
  transition: color 0.15s;
}

.play-close-btn:hover {
  color: #1976d2;
}

.play-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  font-size: 3em;
  color: rgba(255, 255, 255, 0.55);
  background: transparent;
  border: none;
  cursor: pointer;
  z-index: 10;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.15s;
}

.play-arrow:hover {
  color: #1976d2;
}

.play-left {
  left: 36px;
}

.play-right {
  right: 36px;
}

.play-info {
  position: absolute;
  bottom: 24px;
  right: 36px;
  font-size: 1em;
  color: rgba(255, 255, 255, 0.55);
  background: rgba(0, 0, 0, 0.5);
  padding: 8px 12px;
  border-radius: 6px;
}
</style> 