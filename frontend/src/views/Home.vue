<template>
  <div class="home">
    <div class="container">
      <h1>🖥️ Vue版我的电脑</h1>
      <p class="description">
        基于Vue 3 + Vite的Web版Windows"我的电脑"应用<br>
        现代化的前端技术栈，提供更好的用户体验
      </p>

      <div class="version-grid">
        <div class="version-card">
          <div class="version-title">🚀 Vue 3版本</div>
          <div class="version-desc">
            使用Vue 3 Composition API，响应式数据绑定，组件化开发
          </div>
          <router-link to="/computer" class="btn">打开Vue版本</router-link>
        </div>

        <div class="version-card">
          <div class="version-title">⚡ 原版对比</div>
          <div class="version-desc">
            基于Spring Boot + FreeMarker + Template.js的传统实现
          </div>
          <a href="http://localhost:8080" class="btn" target="_blank">打开原版</a>
        </div>
      </div>

      <div class="features">
        <h3>🎯 功能特性</h3>
        <ul>
          <li>📁 左侧显示网络共享位置列表</li>
          <li>📊 右侧展示分类统计信息</li>
          <li>🗂️ 文件夹浏览和导航功能</li>
          <li>🍞 面包屑导航显示当前路径</li>
          <li>🎨 现代化UI设计，响应式布局</li>
          <li>⚡ Vue 3响应式数据绑定</li>
          <li>🔄 组件化开发，易于维护</li>
          <li>🚀 Vite快速构建和热重载</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.container {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  max-width: 600px;
}

h1 {
  color: #333;
  margin-bottom: 30px;
  font-size: 2.5em;
}

.description {
  color: #666;
  margin-bottom: 30px;
  line-height: 1.6;
}

.version-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.version-card {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s ease;
}

.version-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.version-title {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.version-desc {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.4;
}

.features {
  text-align: left;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  margin-top: 20px;
}

.features h3 {
  color: #333;
  margin-bottom: 15px;
}

.features ul {
  color: #666;
  line-height: 1.6;
}

.features li {
  margin-bottom: 5px;
}
</style> 