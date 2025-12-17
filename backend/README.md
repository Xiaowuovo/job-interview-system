# 就业面试辅助系统 - 后端

## 技术栈
- SpringBoot 2.7.14
- Spring Data JPA
- H2 Database (开发环境)
- MySQL (生产环境)
- Maven

## 项目结构
```
backend/
├── src/main/java/com/interview/
│   ├── InterviewSystemApplication.java    # 启动类
│   ├── common/                            # 通用类
│   │   └── Result.java                    # 统一返回结果
│   ├── config/                            # 配置类
│   │   ├── CorsConfig.java                # 跨域配置
│   │   └── DataInitializer.java           # 数据初始化
│   ├── controller/                        # 控制器层
│   │   ├── UserController.java            # 用户管理
│   │   ├── TutorialController.java        # 教程管理
│   │   ├── QuestionController.java        # 题目管理
│   │   ├── TestRecordController.java      # 测试记录
│   │   └── InterviewController.java       # AI面试
│   ├── entity/                            # 实体类
│   │   ├── User.java                      # 用户
│   │   ├── Tutorial.java                  # 教程
│   │   ├── Question.java                  # 题目
│   │   ├── TestRecord.java                # 测试记录
│   │   └── InterviewSession.java          # 面试会话
│   └── repository/                        # 数据访问层
│       ├── UserRepository.java
│       ├── TutorialRepository.java
│       ├── QuestionRepository.java
│       ├── TestRecordRepository.java
│       └── InterviewSessionRepository.java
└── src/main/resources/
    └── application.yml                    # 配置文件
```

## 核心功能

### 1. 用户管理
- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/{id}` - 获取用户信息

### 2. 教程管理
- `GET /api/tutorials` - 获取所有教程
- `GET /api/tutorials/{id}` - 获取教程详情
- `GET /api/tutorials/category/{category}` - 按分类获取教程
- `POST /api/tutorials` - 创建教程（教师）
- `PUT /api/tutorials/{id}` - 更新教程（教师）
- `DELETE /api/tutorials/{id}` - 删除教程（教师）

### 3. 题目管理
- `GET /api/questions` - 获取所有题目
- `GET /api/questions/category/{category}` - 按分类获取题目
- `GET /api/questions/random` - 随机获取题目
- `POST /api/questions/check` - 检查答案
- `POST /api/questions` - 创建题目（教师）

### 4. 测试记录
- `GET /api/test-records/user/{userId}` - 获取用户测试记录
- `POST /api/test-records` - 提交测试记录
- `GET /api/test-records/statistics/{userId}` - 获取统计信息

### 5. AI模拟面试
- `POST /api/interview/start` - 开始面试
- `POST /api/interview/chat` - AI对话
- `POST /api/interview/end/{sessionId}` - 结束面试
- `GET /api/interview/history/{userId}` - 获取面试历史

## 运行说明

### 前置条件
- JDK 1.8 或以上
- Maven 3.6+

### 启动步骤
1. 进入 backend 目录
2. 运行命令：
```bash
mvn spring-boot:run
```

或者使用 IDE 运行 `InterviewSystemApplication.java`

### 访问地址
- 后端服务：http://localhost:8081/api
- H2 控制台：http://localhost:8081/api/h2-console
  - JDBC URL: `jdbc:h2:mem:interview_db`
  - 用户名: `sa`
  - 密码: (空)

### 初始数据
系统启动时会自动初始化以下数据：
- 用户：
  - 学生账号：student / 123456
  - 教师账号：teacher / 123456
- 教程：3篇面试指导教程
- 题目：5道示例题目

## MySQL 配置（生产环境）
修改 `application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/interview_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```
