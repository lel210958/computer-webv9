<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h3>网络位置</h3>
      <button class="add-btn" @click="showAddModal = true" title="添加网络位置">
        <i class="bi bi-plus"></i>
      </button>
    </div>
    <div class="network-locations">
      <div v-if="loading" class="loading">正在加载网络位置...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else>
        <div
          v-for="location in networkLocations"
          :key="location.id"
          class="location-item"
          :class="{ active: isActive(location) }"
          @click="selectLocation(location)"
        >
          <div class="location-icon">
            <i class="bi bi-hdd-network"></i>
          </div>
          <div class="location-info">
            <div class="location-name">{{ location.name }}</div>
            <div class="location-details">{{ location.ip }} ({{ location.user }})</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加网络位置模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加网络位置</h3>
          <button class="close-btn" @click="showAddModal = false">
            <i class="bi bi-x"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="ip">IP地址</label>
            <input 
              id="ip" 
              v-model="newLocation.ip" 
              type="text" 
              placeholder="例如: 192.168.1.100"
              required
            />
          </div>
          <div class="form-group">
            <label for="path">共享名称</label>
            <input 
              id="path" 
              v-model="newLocation.path" 
              type="text" 
              placeholder="例如: shareSpace"
              required
            />
          </div>
          <div class="form-group">
            <label for="username">用户名</label>
            <input 
              id="username" 
              v-model="newLocation.userName" 
              type="text" 
              placeholder="例如: admin"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input 
              id="password" 
              v-model="newLocation.pwd" 
              type="password" 
              placeholder="输入密码"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showAddModal = false">取消</button>
          <button class="save-btn" @click="addNetworkLocation" :disabled="adding">
            {{ adding ? '添加中...' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { networkAPI } from '../api'

export default {
  name: 'NetworkLocations',
  props: {
    currentLocation: {
      type: Object,
      default: null
    }
  },
  emits: ['location-selected'],
  setup(props, { emit }) {
    const networkLocations = ref([])
    const loading = ref(true)
    const error = ref(null)
    const showAddModal = ref(false)
    const adding = ref(false)
    
    const newLocation = ref({
      ip: '',
      path: '',
      userName: '',
      pwd: ''
    })

    const loadNetworkLocations = async () => {
      try {
        loading.value = true
        error.value = null
        const data = await networkAPI.getNetworkLocations()
        networkLocations.value = data.networkLocationList || []
        
        // 默认选择第一个位置
        if (networkLocations.value.length > 0 && !props.currentLocation) {
          emit('location-selected', networkLocations.value[0])
        }
      } catch (err) {
        error.value = '加载网络位置失败'
        console.error('加载网络位置失败:', err)
      } finally {
        loading.value = false
      }
    }

    const selectLocation = (location) => {
      emit('location-selected', location)
    }

    const isActive = (location) => {
      return props.currentLocation && 
             props.currentLocation.id === location.id
    }

    const addNetworkLocation = async () => {
      // 验证必填字段
      if (!newLocation.value.ip || !newLocation.value.path || !newLocation.value.userName) {
        alert('请填写IP地址、共享名称和用户名')
        return
      }

      try {
        adding.value = true
        await networkAPI.addNetworkLocation(newLocation.value)
        
        // 重新加载网络位置列表
        await loadNetworkLocations()
        
        // 清空表单并关闭模态框
        newLocation.value = { ip: '', path: '', userName: '', pwd: '' }
        showAddModal.value = false
        
        alert('网络位置添加成功！')
      } catch (err) {
        console.error('添加网络位置失败:', err)
        alert('添加网络位置失败: ' + (err.message || '未知错误'))
      } finally {
        adding.value = false
      }
    }

    onMounted(() => {
      loadNetworkLocations()
    })

    return {
      networkLocations,
      loading,
      error,
      showAddModal,
      adding,
      newLocation,
      selectLocation,
      isActive,
      addNetworkLocation
    }
  }
}
</script>

<style scoped>
/* 引入Bootstrap Icons */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.sidebar {
  width: 260px;
  background: #f4f6fa;
  border-right: 1px solid #dde3ec;
  box-shadow: 2px 0 8px rgba(30, 80, 200, 0.04);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.sidebar-header {
  padding: 0 20px;
  background: #f8fafc;
  border-bottom: 1px solid #dde3ec;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.sidebar-header h3 {
  color: #1976d2;
  font-size: 1.35em;
  margin: 0;
  font-weight: 700;
  line-height: 60px;
  height: 60px;
  display: flex;
  align-items: center;
}

.add-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #1976d2;
  color: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3em;
  transition: background 0.15s, transform 0.15s;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.15);
  line-height: 1;
  margin: 0;
}

.add-btn:hover {
  background: #1565c0;
  transform: scale(1.05);
}

.add-btn i {
  font-size: 1.2em;
  font-weight: bold;
}

.network-locations {
  flex: 1;
  padding: 8px 0;
  overflow-y: auto;
}

.location-item {
  display: flex;
  align-items: center;
  padding: 10px 22px;
  cursor: pointer;
  border-radius: 8px;
  margin: 2px 8px;
  transition: background 0.18s, box-shadow 0.18s;
  font-size: 1.04em;
  position: relative;
}
.location-item:hover {
  background: #e6f0fa;
}
.location-item.active {
  background: #d2e7fb;
  border-left: 4px solid #1976d2;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.08);
}

.location-icon {
  width: 38px;
  height: 38px;
  background: #e3eefd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  color: #1976d2;
  font-size: 1.4em;
  box-shadow: 0 1px 4px rgba(30, 80, 200, 0.04);
}

.location-info {
  flex: 1;
  min-width: 0;
}

.location-name {
  font-weight: 500;
  color: #222;
  margin-bottom: 2px;
  font-size: 1.08em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.location-details {
  font-size: 0.92em;
  color: #6a7ba2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.loading, .error {
  text-align: center;
  color: #888;
  padding: 30px 0 0 0;
  font-size: 1em;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 400px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px 24px 16px 24px;
  border-bottom: 1px solid #e3e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-header h3 {
  color: #1976d2;
  font-size: 1.2em;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5em;
  color: #666;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.15s;
}

.close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 20px 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
  font-size: 0.95em;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dde3ec;
  border-radius: 6px;
  font-size: 1em;
  transition: border-color 0.15s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.1);
}

.modal-footer {
  padding: 16px 24px 20px 24px;
  border-top: 1px solid #e3e8f0;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn, .save-btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 1em;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid;
}

.cancel-btn {
  background: white;
  color: #666;
  border-color: #dde3ec;
}

.cancel-btn:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.save-btn {
  background: #1976d2;
  color: white;
  border-color: #1976d2;
}

.save-btn:hover:not(:disabled) {
  background: #1565c0;
  border-color: #1565c0;
}

.save-btn:disabled {
  background: #ccc;
  border-color: #ccc;
  cursor: not-allowed;
}
</style> 