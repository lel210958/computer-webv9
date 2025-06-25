# Web版我的电脑

这是一个基于Spring Boot + FreeMarker + Template.js的Web版Windows"我的电脑"应用，采用**视图与数据分离**的架构设计。

## 🎯 架构设计

### 核心原则
- **视图与数据分离**: 页面模板只负责渲染，数据通过AJAX接口获取
- **模块化设计**: 每个功能模块对应独立的模板文件
- **接口驱动**: 每个数据接口对应一个模板渲染函数

### 技术架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端页面      │    │   数据接口      │    │   模板渲染      │
│  computer.ftl   │◄──►│  JSON API       │◄──►│  Template.js    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   页面框架      │    │   数据模型      │    │   模板函数      │
│  HTML + CSS     │    │  Java Beans     │    │  renderXXX()    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 功能特性

- 🖥️ 模拟Windows"我的电脑"界面
- 📁 左侧显示网络共享位置列表
- 🎨 现代化的UI设计
- 📱 响应式布局
- ⚡ 使用Template.js进行模板渲染
- 📊 分类统计信息展示
- 🗂️ 文件夹浏览和导航
- 🔄 视图与数据分离架构

## 技术栈

- **后端**: Spring Boot 2.7.14
- **模板引擎**: FreeMarker
- **前端模板**: Template.js (art-template)
- **样式**: CSS3 + Flexbox + Grid
- **构建工具**: Maven
- **架构模式**: 视图与数据分离

## 项目结构

```
computer-webv9/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/computerweb/
│       │       ├── ComputerWebApplication.java
│       │       ├── controller/
│       │       │   └── ComputerController.java
│       │       └── model/
│       │           ├── NetworkLocation.java
│       │           ├── NetworkLocationResponse.java
│       │           ├── CategoryCount.java
│       │           ├── CategoryCountRequest.java
│       │           ├── CategoryCountResponse.java
│       │           ├── FolderItem.java
│       │           ├── FolderListRequest.java
│       │           └── FolderListResponse.java
│       └── resources/
│           ├── application.yml
│           ├── static/
│           │   └── js/
│           │       └── templates/
│           │           ├── location-template.js
│           │           ├── category-template.js
│           │           ├── folder-template.js
│           │           └── template-manager.js
│           └── templates/
│               ├── index.ftl
│               └── computer.ftl
├── pom.xml
└── README.md
```

## 快速开始

### 1. 环境要求

- Java 8+
- Maven 3.6+

### 2. 运行项目

```bash
# 克隆项目
git clone <repository-url>
cd computer-webv9

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 3. 访问应用

- 主页: http://localhost:8080
- 我的电脑: http://localhost:8080/computer

## API接口

### 1. 获取网络位置列表

- **URL**: `/smc/api/network-locations`
- **方法**: GET
- **响应格式**: JSON

```json
{
  "networkLocationList": [
    {
      "ip": "192.168.95.101",
      "name": "shareSpace",
      "user": "lel0958_share",
      "pwd": ""
    },
    {
      "ip": "192.168.95.101",
      "name": "学习资料",
      "user": "lel0958_share",
      "pwd": ""
    }
  ]
}
```

### 2. 获取分类统计信息

- **URL**: `/smc/api/network-location/category-count`
- **方法**: POST
- **请求参数**: 
```json
{
  "networkLocation": {
    "ip": "192.168.95.101",
    "name": "shareSpace"
  }
}
```
- **响应格式**: JSON
```json
{
  "networkLocation": {
    "ip": "192.168.95.101",
    "name": "shareSpace"
  },
  "categoryCount": [
    {
      "type": "video",
      "count": "102"
    },
    {
      "type": "image",
      "count": "103"
    },
    {
      "type": "doc",
      "count": "104"
    },
    {
      "type": "zip",
      "count": "105"
    },
    {
      "type": "music",
      "count": "106"
    }
  ]
}
```

### 3. 获取文件夹列表

- **URL**: `/smc/api/network-location/folder-list`
- **方法**: POST
- **请求参数**: 
```json
{
  "networkLocation": {
    "ip": "192.168.95.101",
    "name": "shareSpace"
  },
  "currentPath": ""
}
```
- **响应格式**: JSON
```json
{
  "networkLocation": {
    "ip": "192.168.95.101",
    "name": "shareSpace"
  },
  "folderList": [
    {
      "fileName": "apps",
      "isDirectory": "1",
      "fileSize": "0",
      "fileType": "DIRECTORY",
      "filePath": "apps",
      "lastModify": "2025-06-23 05:46:54"
    },
    {
      "fileName": "download",
      "isDirectory": "1",
      "fileSize": "0",
      "fileType": "DIRECTORY",
      "filePath": "download",
      "lastModify": "2025-06-23 05:46:54"
    },
    {
      "fileName": "IPX-534.mp4",
      "isDirectory": "0",
      "fileSize": "6352127652",
      "fileType": "VIDEO",
      "filePath": "IPX-534.mp4",
      "lastModify": "2025-06-23 05:46:54"
    }
  ]
}
```

### 页面路由

- **主页**: `GET /`
- **我的电脑**: `GET /{view}` (view=computer)

## 架构说明

### 1. 视图与数据分离

本项目采用**视图与数据分离**的架构设计：

- **视图层**: FreeMarker模板只负责页面结构和样式
- **数据层**: 通过AJAX接口获取JSON数据
- **渲染层**: 使用Template.js进行客户端模板渲染

### 2. 数据流程

```
1. 页面加载 → computer.ftl
2. JavaScript调用接口 → /smc/api/xxx
3. 后端返回JSON数据 → Java Controller
4. 前端接收数据 → JavaScript
5. 模板渲染 → Template.js
6. 更新DOM → 页面显示
```

### 3. 模板组织

- **主模板**: `computer.ftl` - 页面框架和布局
- **渲染函数**: JavaScript中的`renderXXXTemplate()`函数
- **数据绑定**: 通过AJAX获取的数据直接传递给渲染函数

### 4. 优势

- ✅ **解耦合**: 视图与数据完全分离
- ✅ **可维护性**: 模板和接口独立开发
- ✅ **可扩展性**: 新增功能只需添加接口和模板
- ✅ **性能优化**: 支持局部更新，减少页面刷新
- ✅ **开发效率**: 前后端可以并行开发

## 功能说明

1. **主页**: 简洁的欢迎页面，提供进入"我的电脑"的入口
2. **我的电脑页面**: 
   - 左侧边栏显示网络共享位置列表
   - 默认选择第一个网络位置
   - 面包屑导航显示当前路径
   - 分类统计卡片展示文件类型统计
   - 文件夹列表支持点击导航
   - 使用Template.js动态渲染数据
   - 响应式设计，支持不同屏幕尺寸

## 自定义配置

可以在 `application.yml` 中修改以下配置：

- 服务器端口: `server.port`
- FreeMarker模板配置: `spring.freemarker.*`

## 开发说明

- 使用FreeMarker作为服务端模板引擎
- 前端使用Template.js进行客户端模板渲染
- 采用现代化的CSS布局技术
- 支持热重载开发
- 支持文件夹导航和面包屑导航
- 采用视图与数据分离的架构设计

## 许可证

MIT License 