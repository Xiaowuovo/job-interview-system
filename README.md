# 🎓 就业面试辅助系统

基于 SpringBoot + Vue 的一站式面试准备平台，提供智能题库、AI模拟面试、能力评估等全方位面试辅助服务。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-2.6-4FC08D.svg)](https://vuejs.org/)
[![Element UI](https://img.shields.io/badge/Element%20UI-2.15-409EFF.svg)](https://element.eleme.io/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 项目简介

本项目是一个功能完善的前后端分离Web应用，参考牛客网、LeetCode等主流平台，专为求职者打造的面试备考系统。

### 核心特色

- 🎯 **智能题库系统** - 5种题型，多维度分类，智能推荐
- 🤖 **AI模拟面试** - 技术/行为/HR三类面试，实时对话评分
- 📊 **能力模型评估** - 8+维度能力分析，个性化学习路径
- 📚 **结构化知识库** - 系统化面试知识体系
- 📈 **数据分析报告** - 学习进度、成长曲线、薄弱点分析
- 🔖 **错题本系统** - 艾宾浩斯遗忘曲线，智能复习提醒

## 🎯 主要功能

### 学生端
- ✅ 面试教程学习（基础指导/技术面试/行为面试）
- ✅ 智能题库练习（Java/前端/数据库/算法/系统设计）
- ✅ AI模拟面试（技术面/行为面/HR面）
- ✅ 错题本管理（智能复习提醒）
- ✅ 能力模型评估（多维度能力雷达图）
- ✅ 学习数据分析（进度追踪/成绩统计）

### 教师端
- ✅ 教程管理（创建/编辑/删除）
- ✅ 题目管理（多题型支持/标签分类）
- ✅ 学生数据查看

## 🛠️ 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.14 | 核心框架 |
| Spring Data JPA | 2.7.14 | 数据访问 |
| H2 Database | - | 开发环境 |
| MySQL | 8.0+ | 生产环境 |
| Lombok | - | 简化代码 |
| Jackson | - | JSON处理 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 2.6 | 前端框架 |
| Vue Router | 3.5 | 路由管理 |
| Element UI | 2.15 | UI组件库 |
| Axios | 0.27 | HTTP客户端 |
| ECharts | 5.x | 数据可视化 |

## 📁 项目结构

```
毕业设计-01/
├── backend/                    # 后端项目
│   ├── src/main/java/com/interview/
│   │   ├── entity/            # 实体类（10个核心实体）
│   │   ├── repository/        # 数据访问层
│   │   ├── service/          # 业务逻辑层
│   │   ├── controller/       # 控制器层
│   │   ├── config/           # 配置类
│   │   └── common/           # 通用组件
│   ├── src/main/resources/
│   │   └── application.yml   # 配置文件
│   ├── pom.xml              # Maven配置
│   └── API文档.md            # API接口文档
├── frontend/                  # 前端项目
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── router/          # 路由配置
│   │   └── main.js          # 入口文件
│   ├── public/              # 静态资源
│   └── package.json         # npm配置
├── docs/                     # 文档目录
│   ├── 系统设计文档.md
│   ├── 模块难度评估.md
│   └── 用户角色方案对比.md
├── 实现总结.md               # 实现总结文档
└── README.md                # 本文件
```

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- Node.js 14+
- Maven 3.6+ (可选，推荐使用IDE)

### 方式一：一键启动（推荐）

**Windows用户**
```bash
双击运行 `启动.bat`，选择选项 3
```

### 方式二：手动启动

#### 1. 启动后端

```bash
# 进入backend目录
cd backend

# 使用Maven启动
mvn spring-boot:run

# 或使用IDE直接运行 InterviewSystemApplication.java
```

后端服务将在 **http://localhost:8081** 启动

#### 2. 启动前端

```bash
# 进入frontend目录
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run serve
```

前端服务将在 **http://localhost:8080** 启动

### 访问系统

- **前端地址**: http://localhost:8080
- **后端API**: http://localhost:8081/api
- **H2控制台**: http://localhost:8081/h2-console

### 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 学生 | student | 123456 | 普通学生账号 |
| 教师 | teacher | 123456 | 教师账号 |

## 💡 核心功能说明

### 1. 智能题库系统

**题型支持**
- 单选题 (CHOICE)
- 多选题 (MULTIPLE_CHOICE)
- 编程题 (CODING)
- 简答题 (SHORT_ANSWER)
- 系统设计题 (SYSTEM_DESIGN)

**功能特性**
- 三级难度分类（简单/中等/困难）
- 多维度标签系统
- 随机抽题练习
- 热门题目推荐
- 全文搜索
- 自动统计（浏览量/通过率/平均分）

### 2. AI模拟面试

**面试类型**
- **技术面试** - 技术能力、项目经验、算法问题
- **行为面试** - STAR法则、团队协作、问题解决
- **HR面试** - 职业规划、薪资谈判、个人特质

**核心功能**
- 智能开场白生成
- 基于规则的AI对话
- 对话历史管理
- 自动评分反馈
- 时长统计

### 3. 能力模型评估

**评估维度**
- 算法能力
- 编码能力
- 系统设计能力
- 数据库能力
- 计算机网络
- 操作系统
- 沟通表达
- 问题解决

**能力等级**
- BEGINNER (初级)
- INTERMEDIATE (中级)
- ADVANCED (高级)
- EXPERT (专家)

### 4. 错题本系统

基于**艾宾浩斯遗忘曲线**的智能复习提醒：
- 复习时间点：1天、2天、4天、7天、15天
- 掌握度追踪
- 复习次数统计

## 📊 数据库设计

### 核心数据表（10张）

1. **users** - 用户表（角色/会员/积分）
2. **questions** - 题目表（5种题型/统计数据）
3. **interview_sessions** - 面试会话表（3种面试类型）
4. **answer_records** - 答题记录表
5. **wrong_question_book** - 错题本表
6. **knowledge_points** - 知识点表
7. **study_records** - 学习记录表
8. **user_ability_model** - 用户能力模型表
9. **tutorials** - 教程表
10. **test_records** - 测试记录表

## 📝 API文档

系统提供 **30+** RESTful API接口，详细文档请查看：
- [后端API文档](backend/API文档.md)
- [系统设计文档](docs/系统设计文档.md)

### 核心API示例

```bash
# 用户登录
POST /api/users/login
{
  "username": "student",
  "password": "123456"
}

# 随机获取题目
GET /api/questions/random?category=Java&count=10

# 开始AI面试
POST /api/interview/start
{
  "userId": 1,
  "sessionType": "TECHNICAL",
  "position": "Java开发工程师"
}

# AI对话
POST /api/interview/chat
{
  "sessionId": 1,
  "message": "我有3年Java开发经验..."
}
```

## 🎨 系统截图

（可添加系统截图）

## 🔧 配置说明

### 后端配置

编辑 `backend/src/main/resources/application.yml`

```yaml
server:
  port: 8081

spring:
  datasource:
    # 开发环境使用H2
    url: jdbc:h2:mem:interview_db
    driver-class-name: org.h2.Driver

    # 生产环境切换MySQL
    # url: jdbc:mysql://localhost:3306/interview_db
    # driver-class-name: com.mysql.cj.jdbc.Driver
    # username: root
    # password: your_password
```

### 前端配置

编辑 `frontend/vue.config.js` 配置API代理

## 📈 项目进度

### ✅ 已完成
- [x] 核心实体设计（10个实体类）
- [x] 数据访问层（Repository）
- [x] 业务逻辑层（Service）
- [x] RESTful API（30+接口）
- [x] AI面试核心算法
- [x] 数据初始化
- [x] 基础前端页面

### 🚧 进行中
- [ ] 前端UI优化
- [ ] 数据可视化（ECharts）
- [ ] 推荐算法实现

### 📅 计划中
- [ ] 接入真实AI（OpenAI/文心一言）
- [ ] Redis缓存
- [ ] 简历管理模块
- [ ] 社区功能
- [ ] 数据分析报告
- [ ] 移动端适配

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

**毕业设计项目**

- 系统设计：基于主流面试平台（牛客网、LeetCode）
- 技术栈：Spring Boot + Vue + Element UI
- 功能：智能题库 + AI面试 + 能力评估

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element UI](https://element.eleme.io/)
- [ECharts](https://echarts.apache.org/)

## 📧 联系方式

如有问题或建议，欢迎通过以下方式联系：
- 📮 Issue: [提交Issue](https://github.com/yourname/interview-system/issues)
- 📧 Email: your.email@example.com

---

⭐ 如果这个项目对你有帮助，请给个Star支持一下！
