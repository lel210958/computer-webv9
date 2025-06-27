# Web版我的电脑

这是一个基于Spring Boot + FreeMarker + Template.js的Web版Windows"我的电脑"应用，采用**视图与数据分离**的架构设计。

## 🗄️ 数据库配置

### 1. 创建数据库
```sql
CREATE DATABASE media_share_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 初始化数据库表
运行 `init-database.sql` 脚本或执行以下SQL：

```sql
-- 创建网络共享位置表
CREATE TABLE IF NOT EXISTS `mss_network_location` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ip` VARCHAR(32) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`path` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`user_name` VARCHAR(128) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`pwd` VARCHAR(128) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`create_time` DATETIME NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='网络共享位置'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

-- 创建文件信息表
CREATE TABLE IF NOT EXISTS `mss_file` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`file_name` VARCHAR(255) NOT NULL COMMENT '文件名' COLLATE 'utf8mb4_unicode_ci',
	`file_path` VARCHAR(2048) NOT NULL COMMENT '文件路径' COLLATE 'utf8mb4_unicode_ci',
	`parent_path` VARCHAR(2048) NOT NULL COMMENT '上级路径' COLLATE 'utf8mb4_unicode_ci',
	`file_size` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '文件大小，单位字节',
	`fileType` VARCHAR(32) NOT NULL COMMENT '文件类型' COLLATE 'utf8mb4_unicode_ci',
	`network_location_id` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '网络共享位置id',
	`last_modify` DATETIME NOT NULL COMMENT '最近修改时间',
	`sync_time` DATETIME NOT NULL COMMENT '同步时间',
	`statis_date` DATE NOT NULL COMMENT '分区日期',
	`is_directory` TINYINT(4) UNSIGNED NOT NULL COMMENT '是否是文件夹',
	PRIMARY KEY (`id`) USING BTREE,
	KEY `idx_network_location_id` (`network_location_id`),
	KEY `idx_parent_path` (`parent_path`),
	KEY `idx_file_path` (`file_path`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

-- 插入示例网络位置数据
INSERT INTO `mss_network_location` (`ip`, `path`, `user_name`, `pwd`, `create_time`) VALUES
('192.168.95.100', 'shareSpace', 'lel0958_share', 'lel@210958_SH', NOW()),
('192.168.95.100', '学习资料', 'lel0958_share', 'lel@210958_SH', NOW())
ON DUPLICATE KEY UPDATE `create_time` = NOW();
```

### 3. 配置数据库连接
在 `application.yml` 中配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:9527/media_share_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: mss_user
    password: lel@210958_MS
    driver-class-name: com.mysql.cj.jdbc.Driver
```

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
- 📁 左侧显示网络共享位置列表（从数据库读取）
- 🎨 现代化的UI设计
- 📱 响应式布局
- ⚡ 使用Template.js进行模板渲染
- 📊 分类统计信息展示
- 🗂️ 文件夹浏览和导航
- 🔄 视图与数据分离架构
- 🗄️ 数据库驱动的网络位置管理

## 技术栈

- **后端**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis
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
│       │       │   └── NetworkLocationController.java
│       │       ├── entity/
│       │       │   ├── FolderItem.java
│       │       │   └── NetworkLocationEntity.java
│       │       ├── mapper/
│       │       │   ├── FolderItemMapper.java
│       │       │   └── NetworkLocationMapper.java
│       │       ├── model/
│       │       │   ├── NetworkLocation.java
│       │       │   ├── NetworkLocationResponse.java
│       │       │   ├── CategoryCount.java
│       │       │   ├── CategoryCountRequest.java
│       │       │   ├── CategoryCountResponse.java
│       │       │   ├── FolderListRequest.java
│       │       │   └── FolderListResponse.java
│       │       ├── service/
│       │       │   ├── FileScanService.java
│       │       │   ├── FolderItemService.java
│       │       │   ├── NetworkLocationService.java
│       │       │   └── impl/
│       │       │       ├── FileScanServiceImpl.java
│       │       │       ├── FolderItemServiceImpl.java
│       │       │       └── NetworkLocationServiceImpl.java
│       │       └── utils/
│       │           └── FileUtils.java
│       └── resources/
│           ├── application.yml
│           ├── sql/
│           │   └── create_table.sql
│           └── com/example/computerweb/mapper/
│               ├── FolderItemMapper.xml
│               └── NetworkLocationMapper.xml
├── init-database.sql
├── pom.xml
└── README.md
```

## 快速开始

### 1. 环境要求

- Java 8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE media_share_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 初始化表结构
mysql -u root -p media_share_system < init-database.sql
```

### 3. 运行项目

```bash
# 克隆项目
git clone <repository-url>
cd computer-webv9

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 4. 访问应用

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
      "ip": "192.168.95.100",
      "name": "shareSpace",
      "user": "lel0958_share",
      "pwd": ""
    },
    {
      "ip": "192.168.95.100",
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
  "networkLocationId": 1
}
```
- **响应格式**: JSON
```json
{
  "networkLocationId": 1,
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
  "networkLocationId": 1,
  "currentPath": ""
}
```
- **响应格式**: JSON
```json
{
  "networkLocationId": 1,
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

### 4. 扫描网络位置

- **URL**: `/smc/api/network-location/scan`
- **方法**: POST
- **请求参数**: 
```json
{
  "networkLocationId": 1
}
```
- **响应格式**: JSON
```json
{
  "networkLocationId": 1,
  "scanStatus": "SUCCESS",
  "scannedFiles": 150,
  "existingFiles": 120,
  "newFiles": 30,
  "updatedFiles": 5,
  "deletedFiles": 2,
  "message": "扫描完成"
}
```

**功能说明**:
- 扫描指定网络共享位置下的所有文件和文件夹
- 自动同步到数据库：新增不存在的文件记录，更新已修改的文件记录，删除已不存在的文件记录
- 支持递归扫描子目录
- 自动识别文件类型（VIDEO、IMAGE、MUSIC、DOCUMENT、ZIP、OTHER）
- 记录文件大小、修改时间等信息

### 5. 文件流式播放

- **URL**: `/smc/api/network-location/stream`
- **方法**: GET
- **请求参数**: 
  - `networkLocationId`: 网络位置ID
  - `filePath`: 文件路径
- **功能**: 支持视频和图片的流式播放，支持断点续传

### 6. 添加网络位置

- **URL**: `/smc/api/network-location/add`
- **方法**: POST
- **请求参数**: 
```json
{
  "ip": "192.168.95.100",
  "path": "shareSpace",
  "userName": "lel0958_share",
  "pwd": "lel@210958_SH"
}
```
- **响应格式**: JSON
```json
{
  "success": true,
  "message": "网络位置添加成功",
  "id": 3
}
```

**功能说明**:
- 添加新的网络共享位置配置到数据库
- 支持IP地址、共享名称、用户名和密码配置
- 自动验证必填字段（IP地址、共享名称、用户名）
- 密码字段为可选，为空时使用空字符串
- 返回添加结果和新创建的网络位置ID

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

## 测试和故障排除

### 测试脚本

项目提供了多个测试脚本来帮助诊断问题：

1. **test-service.bat** - 测试后端服务基本功能
2. **test-scan.bat** - 测试扫描功能（基础版本）
3. **test-scan-safe.bat** - 测试扫描功能（安全版本，包含错误处理）

### 常见问题排查

#### 1. NullPointerException 错误

**错误信息**: `java.lang.NullPointerException: null at com.hierynomus.ntlm.functions.NtlmFunctions.NTOWFv2`

**可能原因**:
- 密码为空或null
- 认证信息不完整

**解决方案**:
- 确保密码不为空
- 检查NetworkLocation对象中的密码设置
- 使用正确的用户名和密码

#### 2. 连接失败

**错误信息**: `java.net.ConnectException`

**可能原因**:
- 网络连接问题
- SMB服务未启动
- 防火墙阻止

**解决方案**:
- 检查网络连接
- 确认SMB服务正在运行
- 检查防火墙设置
- 验证IP地址和端口

#### 3. 认证失败

**错误信息**: 包含"authentication"的错误

**可能原因**:
- 用户名或密码错误
- 用户权限不足
- SMB版本不兼容

**解决方案**:
- 验证用户名和密码
- 检查用户权限
- 尝试不同的SMB版本

#### 4. 数据库连接问题

**错误信息**: 数据库相关错误

**可能原因**:
- 数据库服务未启动
- 连接配置错误
- 数据库权限问题

**解决方案**:
- 启动MySQL服务
- 检查数据库连接配置
- 验证数据库用户权限

### 调试步骤

1. **运行基础测试**:
   ```bash
   test-service.bat
   ```

2. **检查日志**:
   - 查看后端控制台日志
   - 检查错误信息和堆栈跟踪

3. **验证网络连接**:
   - 使用ping命令测试网络连通性
   - 检查SMB共享是否可访问

4. **测试扫描功能**:
   ```bash
   test-scan-safe.bat
   ```

### 日志级别

可以在 `application.yml` 中调整日志级别：

```yaml
logging:
  level:
    com.example.computerweb: DEBUG  # 应用日志
    org.mybatis: DEBUG              # SQL日志
    com.hierynomus.smbj: DEBUG      # SMB日志
```

## 开发说明

- 使用FreeMarker作为服务端模板引擎
- 前端使用Template.js进行客户端模板渲染
- 采用现代化的CSS布局技术
- 支持热重载开发
- 支持文件夹导航和面包屑导航
- 采用视图与数据分离的架构设计

## 许可证

MIT License

## 接口参数变更说明

### 版本更新：NetworkLocation对象 → networkLocationId整数

为了提高接口性能和简化参数传递，所有后台接口已从使用NetworkLocation对象参数改为使用networkLocationId整数参数。

#### 变更的接口：

1. **获取分类统计信息** (`/smc/api/network-location/category-count`)
   - 原参数：`{ "networkLocation": { "ip": "...", "name": "..." } }`
   - 新参数：`{ "networkLocationId": 1 }`

2. **获取文件夹列表** (`/smc/api/network-location/folder-list`)
   - 原参数：`{ "networkLocation": { "ip": "...", "name": "..." }, "currentPath": "..." }`
   - 新参数：`{ "networkLocationId": 1, "currentPath": "..." }`

3. **获取分类文件列表** (`/smc/api/network-location/category-files`)
   - 原参数：`{ "networkLocation": { "ip": "...", "name": "..." }, "type": "..." }`
   - 新参数：`{ "networkLocationId": 1, "type": "..." }`

4. **扫描网络位置** (`/smc/api/network-location/scan`)
   - 原参数：`{ "networkLocation": { "ip": "...", "name": "...", "user": "...", "pwd": "..." } }`
   - 新参数：`{ "networkLocationId": 1 }`

5. **文件流式播放** (`/smc/api/network-location/stream`)
   - 原参数：`?ip=...&shareName=...&filePath=...`
   - 新参数：`?networkLocationId=1&filePath=...`

#### 变更的好处：

- ✅ **性能提升**：减少参数传递大小，提高网络传输效率
- ✅ **简化逻辑**：后端直接通过ID查询数据库，无需复杂的对象匹配
- ✅ **数据一致性**：确保使用数据库中的最新配置信息
- ✅ **安全性**：避免前端传递敏感信息（如密码）

#### 前端适配：

前端组件已相应更新，使用`networkLocation.id`替代原来的`networkLocation`对象传递。

#### 测试脚本更新：

测试脚本已更新为使用新的参数格式：
- `test-scan.bat`：使用`{"networkLocationId": 1}`
- `test-scan-safe.bat`：使用`{"networkLocationId": 1}`

#### 注意事项：

1. 确保数据库中存在对应的网络位置记录
2. 网络位置ID从1开始递增
3. 前端需要确保传递的networkLocation对象包含id字段 