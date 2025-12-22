# 就业面试辅助系统 - 部署文档

## 目录
- [系统要求](#系统要求)
- [软件依赖](#软件依赖)
- [快速部署](#快速部署)
- [手动部署](#手动部署)
- [常见问题](#常见问题)
- [端口说明](#端口说明)

---

## 系统要求

| 项目 | 最低要求 | 推荐配置 |
|------|----------|----------|
| 操作系统 | Windows 10 | Windows 10/11 |
| 内存 | 4GB | 8GB+ |
| 磁盘空间 | 2GB | 5GB+ |
| 网络 | 需要联网下载依赖 | - |

---

## 软件依赖

### 1. Java Development Kit (JDK)

| 项目 | 说明 |
|------|------|
| **版本要求** | JDK 8 或更高版本 (推荐 JDK 17) |
| **下载地址** | https://adoptium.net/ |
| **验证命令** | `java -version` |

**安装步骤:**
1. 下载 JDK 安装包
2. 运行安装程序，记住安装路径
3. 配置环境变量:
   - 新建 `JAVA_HOME` = JDK安装路径 (如 `C:\Program Files\Java\jdk-17`)
   - 添加 `%JAVA_HOME%\bin` 到 `PATH`

### 2. Apache Maven

| 项目 | 说明 |
|------|------|
| **版本要求** | Maven 3.6 或更高版本 |
| **下载地址** | https://maven.apache.org/download.cgi |
| **验证命令** | `mvn -v` |

**安装步骤:**
1. 下载 Maven 二进制压缩包 (apache-maven-x.x.x-bin.zip)
2. 解压到任意目录 (如 `C:\Program Files\Apache\maven-3.9.0`)
3. 配置环境变量:
   - 新建 `MAVEN_HOME` = Maven解压路径
   - 添加 `%MAVEN_HOME%\bin` 到 `PATH`

**配置阿里云镜像 (加速下载):**

编辑 `%USERPROFILE%\.m2\settings.xml`，添加:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

### 3. Node.js

| 项目 | 说明 |
|------|------|
| **版本要求** | Node.js 14 或更高版本 (推荐 LTS 版本) |
| **下载地址** | https://nodejs.org/ |
| **验证命令** | `node -v` 和 `npm -v` |

**安装步骤:**
1. 下载 Node.js LTS 版本安装包
2. 运行安装程序，使用默认选项
3. 安装完成后自动配置 PATH

**配置淘宝镜像 (加速下载):**

```bash
npm config set registry https://registry.npmmirror.com
```

---

## 快速部署

### 方式一: 全自动部署 (推荐)

1. 双击运行 `deploy.bat`
2. 按提示操作，脚本会自动:
   - 检查运行环境
   - 配置国内镜像源
   - 安装项目依赖
   - 启动前后端服务

### 方式二: 使用启动脚本

1. 确保已安装所有依赖软件
2. 双击运行 `start.bat`
3. 选择 `[3] 同时启动前后端`

---

## 手动部署

### 步骤 1: 克隆/复制项目

将项目文件夹复制到目标机器。

### 步骤 2: 安装前端依赖

```bash
cd frontend
npm install
```

### 步骤 3: 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

等待看到 `Started InterviewSystemApplication` 表示启动成功。

### 步骤 4: 启动前端服务

新开一个终端:

```bash
cd frontend
npm run serve
```

等待看到 `App running at: http://localhost:8080` 表示启动成功。

### 步骤 5: 访问系统

打开浏览器访问: http://localhost:8080

---

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 8080 | Vue.js 开发服务器 |
| 后端 | 8081 | Spring Boot API 服务 |
| H2 控制台 | 8081/api/h2-console | 数据库管理界面 |

**访问地址:** http://localhost:8080

**测试账号:**
- 学生: `student` / `123456`
- 教师: `teacher` / `123456`

---

## 常见问题

### Q1: 提示 "java 不是内部或外部命令"

**原因:** Java 未安装或未配置环境变量

**解决:**
1. 确认已安装 JDK
2. 配置 `JAVA_HOME` 环境变量
3. 将 `%JAVA_HOME%\bin` 添加到 `PATH`
4. 重新打开命令行窗口

### Q2: 提示 "mvn 不是内部或外部命令"

**原因:** Maven 未安装或未配置环境变量

**解决:**
1. 下载并解压 Maven
2. 配置 `MAVEN_HOME` 环境变量
3. 将 `%MAVEN_HOME%\bin` 添加到 `PATH`
4. 重新打开命令行窗口

### Q3: npm install 下载很慢或失败

**原因:** 默认使用国外 npm 源

**解决:**
```bash
npm config set registry https://registry.npmmirror.com
```

### Q4: Maven 下载依赖很慢

**原因:** 默认使用国外 Maven 仓库

**解决:** 配置阿里云镜像，参考上方 [Apache Maven](#2-apache-maven) 章节

### Q5: 端口被占用

**原因:** 8080 或 8081 端口已被其他程序使用

**解决:**
1. 运行 `start.bat`，选择 `[5] 清理端口占用`
2. 或手动终止占用端口的进程:
   ```bash
   netstat -ano | findstr :8080
   taskkill /F /PID <进程ID>
   ```

### Q6: 前端页面空白或报错

**原因:** 后端服务未启动或启动失败

**解决:**
1. 确认后端服务窗口显示 `Started InterviewSystemApplication`
2. 检查后端日志是否有错误
3. 确认 8081 端口可访问: http://localhost:8081/api/health

### Q7: 登录失败

**原因:** 数据库未初始化或后端未完全启动

**解决:**
1. 等待后端完全启动 (约 30-60 秒)
2. 使用测试账号: `student` / `123456`

---

## 生产环境部署

如需部署到生产环境，建议:

1. **前端打包:**
   ```bash
   cd frontend
   npm run build
   ```
   将 `dist` 目录部署到 Nginx 等 Web 服务器

2. **后端打包:**
   ```bash
   cd backend
   mvn package -DskipTests
   ```
   使用 `target/*.jar` 文件运行:
   ```bash
   java -jar interview-system-1.0.0.jar
   ```

3. **数据库:** 生产环境建议使用 MySQL 替代 H2

---

## 技术支持

如遇到其他问题，请检查:
1. 所有软件版本是否满足要求
2. 环境变量是否正确配置
3. 网络是否正常 (能访问 maven.aliyun.com 和 registry.npmmirror.com)

---

*文档版本: v1.0*
*最后更新: 2024年12月*
