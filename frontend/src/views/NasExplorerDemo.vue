<template>
  <div class="nas-demo">
    <div class="nas-content">
      <div v-if="!currentLocation" class="empty-tip">请选择左侧网络位置</div>
      <template v-else>
        <!-- 分类统计标题（可折叠） -->
        <div class="category-header" @click="toggleCategoryStats">
          <div class="category-title">
            <i class="bi" :class="isCategoryStatsCollapsed ? 'bi-chevron-right' : 'bi-chevron-down'"></i>
            分类
          </div>
        </div>
        
        <!-- 文件分类统计信息 -->
        <div v-show="!isCategoryStatsCollapsed" class="category-stats">
          <div v-if="loadingCategoryStats" class="loading">正在加载分类统计...</div>
          <div v-else-if="errorCategoryStats" class="error">{{ errorCategoryStats }}</div>
          <div v-else-if="categoryStats.length === 0" class="loading">暂无分类统计信息</div>
          <div v-else class="icon-view category-icon-view">
            <div
              v-for="category in categoryStats"
              :key="category.type"
              class="icon-card category-card"
              @click="handleCategoryClick(category.type)"
            >
              <div class="icon-img">
                <i :class="getCategoryIcon(category.type)"></i>
              </div>
              <div class="icon-name">{{ category.count }}</div>
            </div>
          </div>
        </div>
        
        <!-- 文件目录标题（可折叠） -->
        <div class="directory-header" @click="toggleDirectoryContent">
          <div class="directory-title">
            <i class="bi" :class="isDirectoryContentCollapsed ? 'bi-chevron-right' : 'bi-chevron-down'"></i>
            文件目录
          </div>
        </div>
        
        <!-- 文件目录内容 -->
        <div v-show="!isDirectoryContentCollapsed">
          <!-- 面包屑 -->
          <div class="nas-breadcrumb">
            <span class="breadcrumb-item" @click="goRoot">根目录</span>
            <template v-for="(seg, idx) in pathSegments" :key="idx">
              <span v-if="idx >= 0"> / </span>
              <span class="breadcrumb-item" @click="goToPath(idx)">{{ seg }}</span>
            </template>
          </div>
          
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
          <!-- 内容展示 -->
          <div v-if="loading" class="loading">正在加载...</div>
          <div v-else-if="error" class="error">{{ error }}</div>
          <div v-else-if="sortedContent.length === 0" class="loading">当前目录为空</div>
          <div v-else>
            <div v-if="viewMode==='icon'" class="icon-view">
              <div v-for="item in sortedContent" :key="item.filePath" class="icon-card" @click="handleItemClick(item)">
                <div class="icon-img">
                  <i :class="item.isDirectory == 1 ? 'bi bi-folder-fill' : getFileIcon(item)"></i>
                </div>
                <div class="icon-name">{{ item.fileName }}</div>
              </div>
            </div>
            <table v-else class="list-view">
              <thead>
                <tr><th></th><th>名称</th><th>类型</th><th>大小</th></tr>
              </thead>
              <tbody>
                <tr v-for="item in sortedContent" :key="item.filePath" @click="handleItemClick(item)" style="cursor:pointer;">
                  <td style="width:36px;text-align:center;">
                    <i :class="item.isDirectory == 1 ? 'bi bi-folder-fill' : getFileIcon(item)"></i>
                  </td>
                  <td>{{ item.fileName }}</td>
                  <td>{{ item.isDirectory == 1 ? '文件夹' : '文件' }}</td>
                  <td>{{ item.isDirectory == 1 ? '-' : formatFileSize(item.fileSize) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
    </div>
    <div v-if="playUrl" class="play-modal">
      <div class="play-close-btn" @click="playUrl=''">
        <i class="bi bi-x-lg"></i>
      </div>
      <div class="play-arrow play-arrow-left" @click="playMediaAt(playIndex-1)" :class="{disabled: playIndex<=0}" v-if="playIndex>0">
        <i class="bi bi-chevron-left"></i>
      </div>
      <div class="play-arrow play-arrow-right" @click="playMediaAt(playIndex+1)" :class="{disabled: playIndex >= sortedContent.filter(i=>i.fileType==='VIDEO'||i.fileType==='IMAGE').length-1}" v-if="playIndex < sortedContent.filter(i=>i.fileType==='VIDEO'||i.fileType==='IMAGE').length-1">
        <i class="bi bi-chevron-right"></i>
      </div>
      <video v-if="playType==='VIDEO'" :src="playUrl" controls autoplay></video>
      <img v-else-if="playType==='IMAGE'" :src="playUrl" />
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { networkAPI } from '../api'

export default {
  name: 'NasExplorerDemo',
  props: {
    currentLocation: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const router = useRouter()
    
    // 文件目录数据
    const currentPath = ref('')
    const viewMode = ref('icon')
    const contentData = ref([])
    const loading = ref(false)
    const error = ref(null)
    const playUrl = ref('')
    const playType = ref('')
    const playIndex = ref(-1)
    // 排序相关
    const sortField = ref('name') // name, time, size
    const sortOrder = ref('asc')  // asc, desc
    
    // 分类统计相关
    const categoryStats = ref([])
    const loadingCategoryStats = ref(false)
    const errorCategoryStats = ref(null)
    const isCategoryStatsCollapsed = ref(false)
    const isDirectoryContentCollapsed = ref(false)

    // 排序后的内容
    const sortedContent = computed(() => {
      const arr = [...contentData.value]
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

    // 目录分段
    const pathSegments = computed(() => {
      if (!currentPath.value) return []
      // 先将所有反斜杠替换为正斜杠
      return currentPath.value.replaceAll('\\', '/').split('/').filter(Boolean)
    })

    // 根目录
    const goRoot = () => {
      currentPath.value = ''
      loadContent() // 重新加载根目录内容
    }
    // 跳转到某一级目录
    const goToPath = (idx) => {
      const segs = pathSegments.value.slice(0, idx + 1)
      currentPath.value = segs.join('/')
    }
    // 播放指定索引的多媒体文件
    const playMediaAt = (idx) => {
      const mediaItems = sortedContent.value.filter(item => item.fileType === 'VIDEO' || item.fileType === 'IMAGE')
      if (idx < 0 || idx >= mediaItems.length) return
      const item = mediaItems[idx]
      const filePathParam = encodeURIComponent(item.filePath)
      const playUrlStr = `/api/smc/api/network-location/stream?networkLocationId=${props.currentLocation.id}&filePath=${filePathParam}`
      playUrl.value = playUrlStr
      playType.value = item.fileType
      playIndex.value = idx
    }
    // 点击文件夹/文件
    const handleItemClick = async (item) => {
      if (item.isDirectory == 1) {
        currentPath.value = item.filePath
      } else if (item.fileType === 'VIDEO' || item.fileType === 'IMAGE') {
        // 找到当前多媒体文件在同目录下的索引
        const mediaItems = sortedContent.value.filter(i => i.fileType === 'VIDEO' || i.fileType === 'IMAGE')
        const idx = mediaItems.findIndex(i => i.filePath === item.filePath)
        playMediaAt(idx)
      } else {
        alert(`文件: ${item.fileName}`)
      }
    }
    // 文件类型图标
    const getFileIcon = (item) => {
      const iconMap = {
        'VIDEO': 'bi bi-camera-video-fill',
        'IMAGE': 'bi bi-image-fill',
        'DOCUMENT': 'bi bi-file-earmark-text-fill',
        'MUSIC': 'bi bi-music-note-beamed',
        'ZIP': 'bi bi-file-earmark-zip-fill'
      }
      return iconMap[item.fileType] || 'bi bi-file-earmark-fill'
    }
    // 加载目录内容
    const loadContent = async () => {
      if (!props.currentLocation) {
        contentData.value = []
        return
      }
      loading.value = true
      error.value = null
      try {
        const data = await networkAPI.getFolderList(
          props.currentLocation.id,
          currentPath.value
        )
        contentData.value = data.folderList || []
      } catch (err) {
        error.value = '加载文件夹列表失败'
        contentData.value = []
      } finally {
        loading.value = false
      }
    }
    
    // 加载分类统计信息
    const loadCategoryStats = async () => {
      if (!props.currentLocation) {
        categoryStats.value = []
        return
      }
      
      try {
        loadingCategoryStats.value = true
        errorCategoryStats.value = null
        const data = await networkAPI.getCategoryCount(props.currentLocation.id)
        categoryStats.value = data.categoryCount || []
      } catch (err) {
        errorCategoryStats.value = '加载分类统计失败'
        categoryStats.value = []
        console.error('加载分类统计失败:', err)
      } finally {
        loadingCategoryStats.value = false
      }
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
    
    // 监听网络位置和路径变化
    watch([() => props.currentLocation, currentPath], () => {
      if (props.currentLocation) {
        loadContent()
      }
    }, { immediate: true })
    
    // 监听网络位置变化，加载分类统计
    watch(() => props.currentLocation, (newLocation, oldLocation) => {
      if (newLocation && (!oldLocation || newLocation.id !== oldLocation.id)) {
        loadCategoryStats()
      }
    }, { immediate: true })

    const toggleCategoryStats = () => {
      isCategoryStatsCollapsed.value = !isCategoryStatsCollapsed.value
    }

    const toggleDirectoryContent = () => {
      isDirectoryContentCollapsed.value = !isDirectoryContentCollapsed.value
    }

    const handleCategoryClick = async (type) => {
      if (!props.currentLocation) return
      
      // 无刷切换到分类页面，使用路由参数
      router.push({
        path: '/category',
        query: {
          type: type,
          location: JSON.stringify({
            id: props.currentLocation.id
          })
        }
      })
    }

    return {
      currentPath,
      viewMode,
      contentData,
      loading,
      error,
      pathSegments,
      goRoot,
      goToPath,
      handleItemClick,
      getFileIcon,
      playUrl,
      playType,
      playIndex,
      playMediaAt,
      sortField,
      sortOrder,
      sortedContent,
      toggleSortOrder,
      formatFileSize,
      categoryStats,
      loadingCategoryStats,
      errorCategoryStats,
      getCategoryIcon,
      getCategoryName,
      isCategoryStatsCollapsed,
      toggleCategoryStats,
      isDirectoryContentCollapsed,
      toggleDirectoryContent,
      handleCategoryClick
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.nas-demo {
  height: 100%;
  background: #f3f6fb;
  display: flex;
  flex-direction: column;
}

.nas-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
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
.view-switch button.active, .view-switch button:hover {
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
}
.list-view {
  width: 100%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.07);
  border-collapse: collapse;
  font-size: 1em;
}
.list-view th, .list-view td {
  padding: 10px 12px;
  border-bottom: 1px solid #e3e8f0;
  text-align: left;
}
.list-view th {
  background: #f3f6fb;
  color: #1976d2;
  font-weight: 600;
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
.play-modal video, .play-modal img {
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
.play-arrow-left { left: 24px; }
.play-arrow-right { right: 24px; }
.play-arrow:hover, .play-close-btn:hover { color: #1976d2 !important; }
.play-arrow.disabled { opacity: 0.15; pointer-events: none; }
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

/* 分类统计样式 */
.category-stats {
  margin-bottom: 20px;
}

.category-icon-view {
  display: flex;
  flex-wrap: wrap;
  gap: 22px;
}

.category-card {
  /* 继承.icon-card的样式，无需单独设置 */
  /* 保证高度、圆角、阴影、内边距一致 */
  min-width: 110px;
  max-width: 140px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  cursor: pointer;
}

.category-card .icon-img {
  font-size: 2.6em;
  height: 52px;
  margin-bottom: 10px;
  color: #1976d2;
}

.category-card .icon-name {
  font-size: 1.2em;
  font-weight: bold;
  color: #1976d2;
  text-align: center;
  line-height: 1.2;
}

.category-header, .directory-header {
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95em;
  color: #1976d2;
  font-weight: 500;
  margin-bottom: 12px;
  transition: background 0.15s;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #e3e8f0;
}

.category-header:hover, .directory-header:hover {
  background: #e3f2fd;
  border-color: #1976d2;
}

.category-title, .directory-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #0d47a1;
}

.category-title i, .directory-title i {
  font-size: 0.9em;
  color: #1976d2;
  font-weight: bold;
  transition: transform 0.15s;
  display: inline-block;
  width: 16px;
  text-align: center;
}
</style> 