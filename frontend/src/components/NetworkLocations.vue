<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h3>网络位置</h3>
      <p>可用的网络共享位置</p>
    </div>
    <div class="network-locations">
      <div v-if="loading" class="loading">正在加载网络位置...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else>
        <div
          v-for="location in networkLocations"
          :key="`${location.ip}-${location.name}`"
          class="location-item"
          :class="{ active: isActive(location) }"
          @click="selectLocation(location)"
        >
          <div class="location-icon">💻</div>
          <div class="location-info">
            <div class="location-name">{{ location.name }}</div>
            <div class="location-details">{{ location.ip }} ({{ location.user }})</div>
          </div>
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
             props.currentLocation.ip === location.ip && 
             props.currentLocation.name === location.name
    }

    onMounted(() => {
      loadNetworkLocations()
    })

    return {
      networkLocations,
      loading,
      error,
      selectLocation,
      isActive
    }
  }
}
</script>

<style scoped>
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
  padding: 18px 20px 10px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #dde3ec;
}

.sidebar-header h3 {
  color: #1976d2;
  font-size: 1.08em;
  margin-bottom: 6px;
  font-weight: 600;
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
  font-size: 1.7em;
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
</style> 