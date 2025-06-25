<template>
  <div class="quick-access">
    <div
      v-for="item in quickList"
      :key="item.type"
      class="quick-card"
      @click="handleClick(item.type)"
    >
      <div class="quick-icon">{{ item.icon }}</div>
      <div class="quick-name">{{ item.name }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuickAccess',
  emits: ['quick-click'],
  setup(props, { emit }) {
    const quickList = [
      { type: 'doc', name: '文档', icon: '📄' },
      { type: 'image', name: '图片', icon: '🖼️' },
      { type: 'video', name: '视频', icon: '🎥' },
      { type: 'music', name: '音乐', icon: '🎵' },
      { type: 'zip', name: '压缩包', icon: '🗜️' }
    ]
    const handleClick = (type) => emit('quick-click', type)
    return { quickList, handleClick }
  }
}
</script>

<style scoped>
.quick-access {
  display: flex;
  gap: 18px;
  margin-bottom: 28px;
}
.quick-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(30, 80, 200, 0.07);
  padding: 18px 24px;
  text-align: center;
  cursor: pointer;
  border: 1.5px solid #e3e8f0;
  transition: box-shadow 0.18s, background 0.18s;
}
.quick-card:hover {
  background: #f3fafd;
  box-shadow: 0 4px 18px rgba(30, 80, 200, 0.13);
}
.quick-icon {
  font-size: 2.2em;
  margin-bottom: 8px;
}
.quick-name {
  font-size: 1em;
  font-weight: 500;
  color: #1976d2;
}
</style> 