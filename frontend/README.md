# Vue版我的电脑

基于Vue 3 + Vite的Web版Windows"我的电脑"应用，将原有的Spring Boot + FreeMarker项目转换为现代化的前端架构。

## 🎯 项目特点

- **Vue 3**: 使用最新的Composition API
- **Vite**: 快速的构建工具和开发服务器
- **组件化**: 模块化的组件设计
- **响应式**: 响应式数据绑定
- **现代化**: 现代化的前端技术栈

## 🚀 快速开始

### 环境要求

- Node.js 16+
- npm 或 yarn

### 安装依赖

```bash
cd vue-computer
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 📁 项目结构

```
vue-computer/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API服务
│   │   └── index.js       # API配置和方法
│   ├── components/        # 组件
│   │   ├── NetworkLocations.vue    # 网络位置列表
│   │   ├── CategoryStats.vue       # 分类统计
│   │   ├── FolderList.vue          # 文件夹列表
│   │   └── Breadcrumb.vue          # 面包屑导航
│   ├── views/             # 页面
│   │   ├── Home.vue       # 首页
│   │   └── Computer.vue   # 我的电脑页面
│   ├── App.vue            # 根组件
│   ├── main.js            # 入口文件
│   └── style.css          # 全局样式
├── index.html             # HTML模板
├── package.json           # 项目配置
├── vite.config.js         # Vite配置
└── README.md              # 项目说明
```

## 🔧 技术栈

- **Vue 3**: 前端框架
- **Vue Router**: 路由管理
- **Axios**: HTTP客户端
- **Vite**: 构建工具
- **CSS3**: 样式和布局

## 📋 功能特性

### 1. 首页 (Home.vue)
- 项目介绍和功能说明
- 版本对比展示
- 技术特性列表

### 2. 我的电脑页面 (Computer.vue)
- 左侧网络位置列表
- 右侧内容区域
- 面包屑导航
- 分类统计信息
- 文件夹浏览

### 3. 组件化设计

#### NetworkLocations.vue
- 网络位置列表展示
- 位置选择和切换
- 加载状态处理

#### CategoryStats.vue
- 分类统计信息展示
- 动态图标显示
- 响应式布局

#### FolderList.vue
- 文件夹和文件列表
- 文件类型图标
- 点击导航功能

#### Breadcrumb.vue
- 路径导航
- 点击跳转功能
- 动态路径显示

## 🔄 与原版对比

| 特性 | 原版 (Spring Boot) | Vue版 |
|------|-------------------|-------|
| 架构 | 服务端渲染 | 客户端渲染 |
| 模板 | FreeMarker + Template.js | Vue模板 |
| 数据绑定 | 手动DOM操作 | 响应式数据绑定 |
| 组件化 | 字符串拼接 | 组件化开发 |
| 开发体验 | 传统Web开发 | 现代化前端开发 |
| 性能 | 服务端渲染 | 客户端渲染 + 缓存 |

## 🌐 API接口

项目通过代理配置连接到原Spring Boot后端：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### 接口列表

1. **获取网络位置列表**
   - 路径: `/api/smc/api/network-locations`
   - 方法: GET

2. **获取分类统计**
   - 路径: `/api/smc/api/network-location/category-count`
   - 方法: POST

3. **获取文件夹列表**
   - 路径: `/api/smc/api/network-location/folder-list`
   - 方法: POST

## 🎨 样式设计

- 保持与原版一致的视觉设计
- 使用CSS Grid和Flexbox布局
- 响应式设计，支持不同屏幕尺寸
- 现代化的渐变和阴影效果

## 🔧 开发说明

### 组件通信
- 使用Props向下传递数据
- 使用Emit向上传递事件
- 使用Vue Router进行页面导航

### 状态管理
- 使用Vue 3的ref和reactive管理状态
- 组件内部状态独立管理
- 父子组件通过Props和Emit通信

### 错误处理
- API请求统一错误处理
- 用户友好的错误提示
- 加载状态管理

## 🚀 部署

### 构建
```bash
npm run build
```

### 部署到静态服务器
将`dist`目录部署到任何静态文件服务器即可。

### 环境变量
可以在`.env`文件中配置环境变量：

```env
VITE_API_BASE_URL=http://localhost:8080
```

## 📈 性能优化

- Vite快速构建和热重载
- 组件懒加载
- 图片和资源优化
- 代码分割

## 🔮 后续优化

1. **状态管理**: 引入Pinia进行全局状态管理
2. **UI组件库**: 集成Element Plus或Ant Design Vue
3. **TypeScript**: 添加类型支持
4. **单元测试**: 添加Vue Test Utils测试
5. **PWA**: 支持离线访问
6. **国际化**: 多语言支持

## �� 许可证

MIT License 