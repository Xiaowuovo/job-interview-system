# 就业面试辅助系统 - 前端

## 技术栈
- Vue 2.6
- Vue Router 3.5
- Element UI 2.15
- Axios 0.27

## 项目结构
```
frontend/
├── public/
│   └── index.html                  # HTML模板
├── src/
│   ├── main.js                     # 入口文件
│   ├── App.vue                     # 根组件
│   ├── router/
│   │   └── index.js                # 路由配置
│   └── views/                      # 页面组件
│       ├── Login.vue               # 登录页
│       ├── Home.vue                # 主页布局
│       ├── Dashboard.vue           # 仪表盘
│       ├── Tutorials.vue           # 教程列表
│       ├── TutorialDetail.vue      # 教程详情
│       ├── Practice.vue            # 题目练习
│       ├── Test.vue                # 在线测试
│       ├── Interview.vue           # AI模拟面试
│       └── Records.vue             # 成绩记录
├── package.json                    # 项目依赖
└── vue.config.js                   # Vue配置
```

## 核心功能

### 1. 用户认证
- 登录/注册
- 角色区分（学生/教师）
- 本地存储用户信息

### 2. 面试教程学习
- 浏览教程列表
- 按分类筛选
- 查看教程详情
- 教师可添加/编辑教程

### 3. 分类问题测试
- 选择题目分类（Java、前端、数据库、算法）
- 随机抽取题目
- 计时答题
- 查看答案解析
- 成绩统计

### 4. AI模拟面试
- 选择应聘岗位
- 实时对话
- 智能回复
- 面试评分

### 5. 成绩查询
- 测试记录查看
- 面试历史记录
- 数据统计分析

## 运行说明

### 前置条件
- Node.js 14+
- npm 或 yarn

### 安装依赖
```bash
cd frontend
npm install
```

### 开发环境运行
```bash
npm run serve
```

### 生产环境构建
```bash
npm run build
```

### 访问地址
- 前端服务：http://localhost:8080

## 演示账号
- 学生账号：student / 123456
- 教师账号：teacher / 123456

## 页面路由
- `/` - 重定向到登录页
- `/login` - 登录/注册页
- `/home` - 主页框架
  - `/home/dashboard` - 仪表盘
  - `/home/tutorials` - 教程列表
  - `/home/tutorial/:id` - 教程详情
  - `/home/practice` - 题目练习
  - `/home/test/:category` - 在线测试
  - `/home/interview` - AI模拟面试
  - `/home/records` - 成绩记录

## 代理配置
前端开发服务器通过代理访问后端 API：
- 前端地址：http://localhost:8080
- 后端地址：http://localhost:8081/api
- 代理配置：`/api` -> `http://localhost:8081`
