<template>
  <div class="network-scan-page">
    <div class="scan-card">
      <h2>网络共享位置扫描</h2>
      <div class="form-group">
        <label for="location-select">选择网络共享位置：</label>
        <select id="location-select" v-model="selectedLocationKey">
          <option v-for="loc in locations" :key="locKey(loc)" :value="locKey(loc)">
            {{ loc.name }} ({{ loc.ip }})
          </option>
        </select>
      </div>
      <button class="scan-btn" :disabled="!selectedLocation" @click="startScan">
        {{ scanning ? '正在扫描...' : '开始扫描' }}
      </button>
      <div v-if="scanResult" class="scan-result">
        <div>扫描状态：<span :class="scanResult.scanStatus === 'SUCCESS' ? 'success' : 'fail'">{{ scanResult.scanStatus }}</span></div>
        <div>位置：{{ scanResult.networkLocation.name }} ({{ scanResult.networkLocation.ip }})</div>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { networkAPI } from '../api'

export default {
  name: 'NetworkScan',
  setup() {
    const locations = ref([])
    const selectedLocationKey = ref('')
    const scanning = ref(false)
    const scanResult = ref(null)
    const error = ref('')

    const locKey = (loc) => `${loc.ip}__${loc.name}`
    const selectedLocation = computed(() => {
      return locations.value.find(loc => locKey(loc) === selectedLocationKey.value)
    })

    const loadLocations = async () => {
      try {
        const data = await networkAPI.getNetworkLocations()
        locations.value = data.networkLocationList || []
        if (locations.value.length > 0) {
          selectedLocationKey.value = locKey(locations.value[0])
        }
      } catch (e) {
        error.value = '加载网络位置失败'
      }
    }

    const startScan = async () => {
      if (!selectedLocation.value) return
      scanning.value = true
      scanResult.value = null
      error.value = ''
      try {
        const res = await networkAPI.networkScan({
          ip: selectedLocation.value.ip,
          name: selectedLocation.value.name
        })
        scanResult.value = res
      } catch (e) {
        error.value = '扫描失败，请重试'
      } finally {
        scanning.value = false
      }
    }

    onMounted(() => {
      loadLocations()
    })

    return {
      locations,
      selectedLocationKey,
      selectedLocation,
      scanning,
      scanResult,
      error,
      locKey,
      startScan
    }
  }
}
</script>

<style scoped>
.network-scan-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e3eefd 0%, #fafdff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.scan-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(30, 80, 200, 0.10);
  padding: 38px 38px 32px 38px;
  min-width: 340px;
  max-width: 400px;
  text-align: center;
}
.form-group {
  margin-bottom: 24px;
  text-align: left;
}
label {
  font-weight: 500;
  color: #1976d2;
  margin-bottom: 8px;
  display: block;
}
select {
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1.5px solid #dde3ec;
  font-size: 1em;
  margin-top: 6px;
  margin-bottom: 8px;
}
.scan-btn {
  background: linear-gradient(90deg, #e3eefd 0%, #c3d3ee 100%);
  color: #1976d2;
  border: none;
  border-radius: 10px;
  padding: 10px 32px;
  font-size: 1.1em;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.06);
  transition: background 0.2s, box-shadow 0.2s, color 0.2s;
  margin-top: 10px;
}
.scan-btn:disabled {
  background: #e3eefd;
  color: #aaa;
  cursor: not-allowed;
}
.scan-result {
  margin-top: 28px;
  font-size: 1.08em;
  color: #1976d2;
  background: #f3fafd;
  border-radius: 8px;
  padding: 16px 10px;
}
.scan-result .success {
  color: #43a047;
  font-weight: bold;
}
.scan-result .fail {
  color: #e53935;
  font-weight: bold;
}
.error {
  color: #e53935;
  margin-top: 18px;
}
</style> 