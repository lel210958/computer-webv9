<template>
  <div class="portal-home">
    <header class="portal-header">
      <div class="logo-area">
        <span class="logo">🧩</span>
        <span class="site-title">Web 插件门户</span>
      </div>
      <div class="welcome">欢迎使用你的 Web 插件应用中心</div>
    </header>
    <main class="portal-main">
      <div class="apps-title">全部插件应用</div>
      <div class="app-grid">
        <div class="app-card">
          <div class="app-icon">🗂️</div>
          <div class="app-title">共享中心</div>
          <div class="app-desc">访问和管理你的NAS/SMB网络共享文件</div>
          <router-link to="/nas-demo" class="btn">进入共享中心</router-link>
        </div>
        <!-- 未来可扩展更多应用卡片 -->
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.portal-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #e3eefd 0%, #fafdff 100%);
  display: flex;
  flex-direction: column;
}
.portal-header {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 38px 0 28px 0;
  text-align: center;
  box-shadow: 0 4px 18px rgba(30, 80, 200, 0.10);
}
.logo-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-size: 2.2em;
  font-weight: bold;
  margin-bottom: 10px;
}
.logo {
  font-size: 1.5em;
}
.site-title {
  font-size: 1.1em;
  letter-spacing: 2px;
}
.welcome {
  font-size: 1.15em;
  opacity: 0.92;
  margin-top: 6px;
}
.portal-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0 60px 0;
}
.apps-title {
  font-size: 1.3em;
  color: #1976d2;
  font-weight: 600;
  margin-bottom: 32px;
  letter-spacing: 1px;
}
.app-grid {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
}
.app-card {
  background: #fff;
  border: 2px solid #e9ecef;
  border-radius: 14px;
  padding: 32px 36px 26px 36px;
  transition: all 0.3s ease;
  min-width: 220px;
  max-width: 260px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
}
.app-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.13);
}
.app-icon {
  font-size: 2.8em;
  margin-bottom: 12px;
}
.app-title {
  font-size: 1.18em;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 8px;
}
.app-desc {
  font-size: 0.98em;
  color: #666;
  margin-bottom: 18px;
  line-height: 1.5;
}
.btn {
  background: linear-gradient(90deg, #e3eefd 0%, #c3d3ee 100%);
  color: #1976d2;
  border: none;
  border-radius: 10px;
  padding: 8px 20px;
  font-size: 1em;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.06);
  transition: background 0.2s, box-shadow 0.2s, color 0.2s;
  text-decoration: none;
  display: inline-block;
}
.btn:hover {
  background: #d0e3fa;
  color: #0d47a1;
  box-shadow: 0 4px 16px rgba(30, 80, 200, 0.10);
}
</style> 