import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './views/Home.vue'
import Computer from './views/Computer.vue'
import NasExplorerDemo from './views/NasExplorerDemo.vue'
import CategoryFiles from './views/CategoryFiles.vue'
import './style.css'

// 路由配置
const routes = [
  { path: '/', component: Home },
  { path: '/computer', component: Computer },
  { path: '/nas-demo', component: NasExplorerDemo },
  { path: '/category', component: CategoryFiles }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app') 