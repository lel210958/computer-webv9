<template>
  <div class="computer">
    <div class="header">
      <button class="back-btn" @click="goHome">← 返回</button>
      <h1>我的电脑 (Vue 3版本)</h1>
    </div>

    <div class="main-container">
      <!-- 左侧网络位置列表 -->
      <NetworkLocations 
        :current-location="currentNetworkLocation"
        @location-selected="handleLocationSelected"
      />

      <!-- 右侧内容区域 -->
      <div class="content-area">
        <div class="content-header">
          <div class="content-title">
            {{ currentNetworkLocation ? currentNetworkLocation.name : '欢迎使用我的电脑' }}
          </div>
          <div class="content-subtitle">
            {{ currentNetworkLocation ? `${currentNetworkLocation.ip} (${currentNetworkLocation.user})` : '请从左侧选择网络位置进行浏览' }}
          </div>
        </div>
        
        <!-- 信息框 -->
        <div class="info-box">
          <h4>📋 架构说明</h4>
          <p><strong>渲染方式:</strong> Vue 3响应式数据绑定</p>
          <p><strong>数据获取:</strong> Axios调用接口</p>
          <p><strong>组件化:</strong> 模块化组件开发</p>
          <p><strong>排序功能:</strong> 支持按名称、时间、大小升降序排序</p>
          <p><strong>当前状态:</strong> {{ getStatusText() }}</p>
        </div>

        <!-- 方案A：根目录时显示快捷入口卡片 -->
        <QuickAccess v-if="currentNetworkLocation && currentPath === '' && !currentCategory" @quick-click="handleQuickClick" />
        <!-- 分类视图时显示返回全部按钮 -->
        <div v-if="currentCategory" style="margin-bottom:18px;">
          <button class="btn" @click="clearCategory">← 返回全部文件</button>
        </div>
        
        <!-- 面包屑导航 -->
        <Breadcrumb 
          :current-path="currentPath"
          @path-change="handlePathChange"
        />
        
        <!-- 文件夹列表，支持分类过滤 -->
        <FolderList 
          :network-location="currentNetworkLocation"
          :current-path="currentPath"
          :filter-type="currentCategory"
          @folder-click="handleFolderClick"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import NetworkLocations from '../components/NetworkLocations.vue'
import FolderList from '../components/FolderList.vue'
import Breadcrumb from '../components/Breadcrumb.vue'
import QuickAccess from '../components/QuickAccess.vue'

export default {
  name: 'Computer',
  components: {
    NetworkLocations,
    FolderList,
    Breadcrumb,
    QuickAccess
  },
  setup() {
    const router = useRouter()
    const currentNetworkLocation = ref(null)
    const currentPath = ref('')
    const currentCategory = ref('')

    const goHome = () => {
      router.push('/')
    }

    const handleLocationSelected = (location) => {
      currentNetworkLocation.value = location
      currentPath.value = '' // 重置路径
      currentCategory.value = ''
    }

    const handlePathChange = (path) => {
      currentPath.value = path
      currentCategory.value = ''
    }

    const handleFolderClick = (folder) => {
      if (folder.isDirectory === '1') {
        // 如果是文件夹，进入该文件夹
        const newPath = currentPath.value === '' ? folder.filePath : `${currentPath.value}/${folder.filePath}`
        currentPath.value = newPath
        currentCategory.value = ''
      } else {
        // 如果是文件，显示提示
        alert(`文件: ${folder.fileName}`)
      }
    }

    // 快捷入口点击
    const handleQuickClick = (type) => {
      currentCategory.value = type
    }
    // 返回全部
    const clearCategory = () => {
      currentCategory.value = ''
    }

    const getStatusText = () => {
      if (!currentNetworkLocation.value) {
        return '等待选择网络位置'
      }
      if (currentPath.value === '' && !currentCategory.value) {
        return `已选择: ${currentNetworkLocation.value.name}`
      }
      if (currentCategory.value) {
        return `分类视图: ${currentCategory.value}`
      }
      return `当前位置: ${currentPath.value}`
    }

    return {
      currentNetworkLocation,
      currentPath,
      currentCategory,
      goHome,
      handleLocationSelected,
      handlePathChange,
      handleFolderClick,
      handleQuickClick,
      clearCategory,
      getStatusText
    }
  }
}
</script>

<style scoped>
.computer {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(90deg, #1976d2 0%, #63a4ff 100%);
  color: #fff;
  padding: 16px 28px 16px 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(30, 80, 200, 0.10);
  border-bottom: 1.5px solid #e3e8f0;
}

.header h1 {
  font-size: 1.5em;
  margin-left: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(30, 80, 200, 0.10);
}

.back-btn {
  background: rgba(255, 255, 255, 0.18);
  border: none;
  color: #fff;
  padding: 8px 18px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1em;
  font-weight: 500;
  margin-right: 2px;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
  box-shadow: 0 1px 6px rgba(30, 80, 200, 0.10);
}
.back-btn:hover {
  background: rgba(255, 255, 255, 0.32);
  color: #1976d2;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.content-area {
  flex: 1;
  background: #fafdff;
  padding: 20px;
  overflow-y: auto;
}

.content-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.content-title {
  font-size: 1.5em;
  color: #333;
  margin-bottom: 5px;
  font-weight: 600;
}

.content-subtitle {
  color: #666;
  font-size: 0.98em;
}

.info-box {
  background: #e3f2fd;
  border: 1px solid #2196f3;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.info-box h4 {
  color: #1976d2;
  margin-bottom: 10px;
}

.info-box p {
  color: #333;
  margin-bottom: 5px;
}
</style> 