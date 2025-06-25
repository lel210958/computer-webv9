<template>
  <div class="main-layout">
    <div class="top-bar">
      <div class="logo">🖥️ 我的电脑</div>
      <div class="top-title">Web 文件管理系统</div>
    </div>
    <div class="main-body">
      <NetworkLocations
        :currentLocation="currentLocation"
        @location-selected="handleLocationSelected"
      />
      <div class="main-content">
        <router-view :key="$route.fullPath" :currentLocation="currentLocation" />
      </div>
    </div>
  </div>
</template>

<script>
import NetworkLocations from './NetworkLocations.vue'
import { ref } from 'vue'

export default {
  name: 'MainLayout',
  components: { NetworkLocations },
  setup() {
    const currentLocation = ref(null)
    const handleLocationSelected = (loc) => {
      currentLocation.value = loc
    }
    return { currentLocation, handleLocationSelected }
  }
}
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.top-bar {
  height: 56px;
  background: linear-gradient(90deg, #1976d2 0%, #63a4ff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 28px;
  font-size: 1.15em;
  font-weight: 600;
  box-shadow: 0 2px 12px rgba(30, 80, 200, 0.10);
}
.logo {
  font-size: 1.5em;
  margin-right: 18px;
}
.top-title {
  font-size: 1.1em;
  letter-spacing: 1px;
}
.main-body {
  flex: 1;
  display: flex;
  min-height: 0;
}
.main-content {
  flex: 1;
  background: #f7fafd;
  overflow: auto;
  padding: 0 0 0 0;
}
</style> 