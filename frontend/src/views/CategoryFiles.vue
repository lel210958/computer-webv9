<template>
  <div class="category-files">
    <div class="category-header">
      <div class="back-btn" @click="goBack">
        <i class="bi bi-arrow-left"></i> 返回文件目录
      </div>
      <div class="category-title">
        <div class="category-icon">
          <i :class="getCategoryIcon(categoryType)"></i>
        </div>
        {{ getCategoryName(categoryType) }}文件
      </div>
      <!-- 搜索框 -->
      <div class="search-box">
        <input 
          v-model="searchKeyword" 
          type="text" 
          placeholder="搜索文件名..."
          @input="handleSearch"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">
          <i class="bi bi-search"></i>
        </button>
      </div>
    </div>

    <div class="category-content">
      <div v-if="!currentLocation" class="empty-tip">请选择左侧网络位置</div>
      <template v-else>
        <!-- 视图切换和排序控件 -->
        <div class="view-controls">
          <div class="view-switch">
            <button :class="{active:viewMode==='icon'}" @click="viewMode='icon'">图标视图</button>
            <button :class="{active:viewMode==='list'}" @click="viewMode='list'">列表视图</button>
          </div>
          <div class="sort-controls">
            <span class="sort-label">排序方式:</span>
            <select v-model="sortField" class="sort-select">
              <option value="name">名称</option>
              <option value="time">时间</option>
              <option value="size">大小</option>
            </select>
            <button class="sort-btn" @click="toggleSortOrder" :title="sortOrder==='asc'?'升序':'降序'">
              <i class="bi" :class="sortOrder==='asc'?'bi-arrow-up':'bi-arrow-down'"></i>
            </button>
          </div>
        </div>

        <!-- 文件列表 -->
        <div v-if="loading && files.length === 0" class="loading">正在加载{{ getCategoryName(categoryType) }}文件...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else-if="sortedFiles.length === 0" class="empty">
          {{ searchKeyword ? '未找到匹配的文件' : `暂无${getCategoryName(categoryType)}文件` }}
        </div>
        <div v-else>
          <!-- 图标视图 -->
          <div v-if="viewMode === 'icon'" class="icon-view">
            <div 
              v-for="file in sortedFiles" 
              :key="file.filePath" 
              class="icon-card" 
              @click="handleFileClick(file)"
            >
              <div class="icon-img">
                <i :class="getFileIcon(file)"></i>
              </div>
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
                  <i :class="getFileIcon(file)"></i>
                </td>
                <td class="file-name">{{ file.fileName }}</td>
                <td class="file-size">{{ formatFileSize(file.fileSize) }}</td>
                <td class="file-time">{{ formatTime(file.lastModify) }}</td>
              </tr>
            </tbody>
          </table>
          
          <!-- 加载更多 -->
          <div v-if="hasMore && !loading" class="load-more" @click="loadMore">
            <i class="bi bi-arrow-down-circle"></i>
            加载更多
          </div>
          <div v-if="loading && files.length > 0" class="loading-more">
            <i class="bi bi-arrow-clockwise"></i>
            正在加载更多...
          </div>
          <div v-if="!hasMore && files.length > 0" class="no-more">
            已加载全部文件
          </div>
        </div>
      </template>
    </div>

    <!-- 播放模态框 -->
    <div v-if="playUrl" class="play-modal">
      <div class="play-close-btn" @click="playUrl = ''">
        <i class="bi bi-x-lg"></i>
      </div>
      
      <!-- 左箭头 -->
      <div v-if="mediaFiles.length > 1" class="play-arrow play-left" @click="playMediaAt(playIndex - 1)">
        <i class="bi bi-chevron-left"></i>
      </div>
      
      <!-- 右箭头 -->
      <div v-if="mediaFiles.length > 1" class="play-arrow play-right" @click="playMediaAt(playIndex + 1)">
        <i class="bi bi-chevron-right"></i>
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
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { networkAPI } from '../api'

export default {
  name: 'CategoryFiles',
  props: {
    currentLocation: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    
    // 从查询参数获取分类类型
    const categoryType = ref(route.query.type || '')
    
    // 文件列表相关
    const files = ref([])
    const loading = ref(false)
    const error = ref(null)
    const viewMode = ref('icon')
    const sortField = ref('name')
    const sortOrder = ref('asc')
    
    // 播放相关
    const playUrl = ref('')
    const playType = ref('')
    const playIndex = ref(-1)
    
    // 搜索相关
    const searchKeyword = ref('')
    const hasMore = ref(true)
    const currentPage = ref(1)
    const pageSize = ref(30)
    let searchTimer = null
    
    // 排序后的文件列表
    const sortedFiles = computed(() => {
      const arr = [...files.value]
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
    
    // 多媒体文件列表
    const mediaFiles = computed(() => {
      return sortedFiles.value.filter(file => 
        file.fileType === 'VIDEO' || file.fileType === 'IMAGE'
      )
    })
    
    const toggleSortOrder = () => {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    }
    
    // 加载分类文件
    const loadCategoryFiles = async (isLoadMore = false) => {
      if (!props.currentLocation || !categoryType.value) {
        error.value = '缺少必要参数'
        return
      }
      
      try {
        if (!isLoadMore) {
          loading.value = true
          currentPage.value = 1
          files.value = []
        }
        error.value = null
        
        const data = await networkAPI.getCategoryFiles(
          props.currentLocation.id, 
          categoryType.value,
          searchKeyword.value || null,
          currentPage.value,
          pageSize.value
        )
        
        const newFiles = data.folderList || []
        
        if (isLoadMore) {
          files.value.push(...newFiles)
        } else {
          files.value = newFiles
        }
        
        // 判断是否还有更多数据
        hasMore.value = newFiles.length === pageSize.value
        
      } catch (err) {
        error.value = `加载${getCategoryName(categoryType.value)}文件失败`
        console.error('加载分类文件失败:', err)
      } finally {
        loading.value = false
      }
    }
    
    // 返回文件目录页面
    const goBack = () => {
      router.push('/nas-demo')
    }
    
    // 获取分类图标
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
        'VIDEO': 'bi bi-camera-video-fill',
        'IMAGE': 'bi bi-image-fill',
        'DOCUMENT': 'bi bi-file-earmark-text-fill',
        'MUSIC': 'bi bi-music-note-beamed',
        'ZIP': 'bi bi-file-earmark-zip-fill'
      }
      return iconMap[file.fileType] || 'bi bi-file-earmark-fill'
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
    const formatTime = (time) => {
      if (!time) return '-'
      return new Date(time).toLocaleString('zh-CN')
    }
    
    // 处理文件点击
    const handleFileClick = (file) => {
      if (file.fileType === 'VIDEO' || file.fileType === 'IMAGE') {
        const mediaItems = mediaFiles.value
        const idx = mediaItems.findIndex(item => item.filePath === file.filePath)
        playMediaAt(idx)
      } else {
        alert(`文件: ${file.fileName}`)
      }
    }
    
    // 播放指定索引的多媒体文件
    const playMediaAt = (idx) => {
      const mediaItems = mediaFiles.value
      if (idx < 0 || idx >= mediaItems.length) return
      const item = mediaItems[idx]
      const filePathParam = encodeURIComponent(item.filePath)
      const playUrlStr = `/api/smc/api/network-location/stream?networkLocationId=${props.currentLocation.id}&filePath=${filePathParam}`
      playUrl.value = playUrlStr
      playType.value = item.fileType
      playIndex.value = idx
    }
    
    // 监听网络位置变化
    watch(() => props.currentLocation, (newLocation, oldLocation) => {
      if (newLocation && (!oldLocation || newLocation.id !== oldLocation.id)) {
        loadCategoryFiles()
      }
    }, { immediate: true })
    
    // 监听路由参数变化
    watch(() => route.query.type, (newType) => {
      if (newType && newType !== categoryType.value) {
        categoryType.value = newType
        loadCategoryFiles()
      }
    }, { immediate: true })
    
    // 监听搜索关键词变化
    watch(() => searchKeyword.value, () => {
      if (searchTimer) {
        clearTimeout(searchTimer)
      }
      searchTimer = setTimeout(() => {
        loadCategoryFiles()
      }, 500)
    })
    
    // 滚动到底部自动加载
    const handleScroll = () => {
      const container = document.querySelector('.category-content')
      if (!container) return
      
      const { scrollTop, scrollHeight, clientHeight } = container
      if (scrollTop + clientHeight >= scrollHeight - 100) { // 距离底部100px时加载
        if (hasMore.value && !loading.value) {
          loadMore()
        }
      }
    }
    
    // 组件挂载时添加滚动监听
    onMounted(() => {
      const container = document.querySelector('.category-content')
      if (container) {
        container.addEventListener('scroll', handleScroll)
      }
    })
    
    // 组件卸载时移除滚动监听
    onUnmounted(() => {
      const container = document.querySelector('.category-content')
      if (container) {
        container.removeEventListener('scroll', handleScroll)
      }
      if (searchTimer) {
        clearTimeout(searchTimer)
      }
    })
    
    // 处理搜索（带防抖）
    const handleSearch = () => {
      // 搜索逻辑已在watch中处理，这里可以留空或添加其他逻辑
    }
    
    // 加载更多
    const loadMore = async () => {
      if (loading.value || !hasMore.value) return
      
      currentPage.value++
      await loadCategoryFiles(true)
    }
    
    return {
      categoryType,
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
      searchKeyword,
      hasMore,
      toggleSortOrder,
      loadCategoryFiles,
      goBack,
      getCategoryIcon,
      getCategoryName,
      getFileIcon,
      formatFileSize,
      formatTime,
      handleFileClick,
      playMediaAt,
      handleSearch,
      loadMore
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.category-files {
  height: 100%;
  background: #f3f6fb;
  display: flex;
  flex-direction: column;
}

.category-header {
  background: #fff;
  border-bottom: 1px solid #e3e8f0;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #1976d2;
  cursor: pointer;
  font-weight: 500;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.15s;
  font-size: 0.95em;
}

.back-btn:hover {
  background: #e3f2fd;
}

.back-btn i {
  font-size: 1.1em;
  font-weight: bold;
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
  color: #1976d2;
}

.category-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
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
  margin-right: 10px;
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
  color: #1976d2;
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
  color: #666;
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
  background: #f3f6fb;
}

.file-icon {
  width: 36px;
  text-align: center;
  color: #1976d2;
}

.file-name {
  font-weight: 500;
}

.file-size {
  color: #666;
}

.file-time {
  color: #666;
  font-size: 0.9em;
}

.empty-tip {
  color: #888;
  font-size: 1.1em;
  text-align: center;
  margin-top: 80px;
}

.loading {
  color: #888;
  font-size: 1.1em;
  text-align: center;
  margin-top: 80px;
}

.error {
  color: #ff0000;
  font-size: 1.1em;
  text-align: center;
  margin-top: 80px;
}

.empty {
  color: #888;
  font-size: 1.1em;
  text-align: center;
  margin-top: 80px;
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
  margin: 0;
  padding: 0;
}

.play-modal video,
.play-modal img {
  max-width: 100vw;
  max-height: 100vh;
  width: auto;
  height: 100vh;
  display: block;
  margin: auto;
  box-shadow: 0 0 0 0 transparent;
  background: #000;
  border-radius: 0;
  object-fit: contain;
}

.play-close-btn {
  position: absolute;
  top: 24px;
  right: 36px;
  font-size: 2em;
  color: rgba(255,255,255,0.55);
  background: transparent;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  border: none;
  box-shadow: none;
  transition: color 0.15s;
}

.play-close-btn:hover {
  color: #1976d2;
  background: transparent;
}

.play-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  font-size: 2.6em;
  color: rgba(255,255,255,0.55);
  border: none;
  box-shadow: none;
  user-select: none;
  transition: color 0.15s;
}

.play-left { left: 24px; }
.play-right { right: 24px; }
.play-arrow:hover, .play-close-btn:hover { color: #1976d2 !important; }
.play-info {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255,255,255,0.8);
  font-size: 1.1em;
  background: rgba(0,0,0,0.5);
  padding: 8px 16px;
  border-radius: 20px;
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

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.search-box input {
  width: 200px;
  padding: 8px 12px;
  border: 1px solid #dde3ec;
  border-radius: 6px;
  font-size: 0.9em;
  transition: border-color 0.15s;
}

.search-box input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.1);
}

.search-btn {
  background: #1976d2;
  border: 1px solid #1976d2;
  color: white;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  transition: background 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-btn:hover {
  background: #1565c0;
  border-color: #1565c0;
}

.load-more,
.loading-more,
.no-more {
  text-align: center;
  padding: 20px;
  margin-top: 20px;
  color: #666;
  font-size: 0.95em;
  cursor: pointer;
  transition: color 0.15s;
}

.load-more {
  color: #1976d2;
  border: 1px solid #e3e8f0;
  border-radius: 8px;
  background: #f8fafc;
}

.load-more:hover {
  background: #e3f2fd;
  color: #0d47a1;
}

.load-more i,
.loading-more i {
  margin-right: 8px;
  font-size: 1.1em;
}

.loading-more i {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.no-more {
  color: #999;
  font-style: italic;
}
</style> 