# 就业面试辅助系统 API 文档

## 系统概述
基于 Spring Boot 2.7.14 的后端服务，提供面试准备、在线测试、AI模拟面试等功能。

## 技术栈
- Spring Boot 2.7.14
- Spring Data JPA
- H2 Database / MySQL
- Lombok
- Jackson

## 核心模块

### 1. 用户模块 (UserController)

#### 1.1 用户注册
```
POST /api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户",
  "targetPosition": "Java开发工程师"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "STUDENT",
    ...
  }
}
```

#### 1.2 用户登录
```
POST /api/users/login
Content-Type: application/json

{
  "username": "student",
  "password": "123456"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "student",
    "role": "STUDENT",
    ...
  }
}
```

#### 1.3 获取用户信息
```
GET /api/users/{id}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "student",
    "nickname": "学生用户",
    "targetPosition": "Java开发工程师",
    ...
  }
}
```

#### 1.4 更新用户信息
```
PUT /api/users/{id}
Content-Type: application/json

{
  "nickname": "新昵称",
  "targetPosition": "前端开发工程师",
  "email": "newemail@example.com"
}
```

#### 1.5 获取用户能力模型
```
GET /api/users/{id}/ability

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "algorithmAbility": 60.0,
    "codingAbility": 70.0,
    "databaseAbility": 65.0,
    "overallScore": 0.0,
    "level": "BEGINNER"
  }
}
```

---

### 2. 题目模块 (QuestionController)

#### 2.1 获取所有题目
```
GET /api/questions

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "String数据类型",
      "content": "Java中，String是基本数据类型吗？",
      "type": "CHOICE",
      "difficulty": "EASY",
      "category": "Java",
      "optionA": "是",
      "optionB": "否",
      "optionC": "取决于使用场景",
      "optionD": "JDK版本不同结果不同",
      "correctAnswer": "B",
      ...
    }
  ]
}
```

#### 2.2 根据ID获取题目
```
GET /api/questions/{id}
```

#### 2.3 根据分类获取题目
```
GET /api/questions/category/{category}

示例: GET /api/questions/category/Java
```

#### 2.4 随机获取题目
```
GET /api/questions/random?category=Java&count=10

参数:
- category: 可选，题目分类
- count: 默认10，题目数量
```

#### 2.5 搜索题目
```
GET /api/questions/search?keyword=Java

参数:
- keyword: 搜索关键词，会在标题和内容中搜索
```

#### 2.6 获取热门题目
```
GET /api/questions/hot

返回浏览量最高的100道题目
```

#### 2.7 检查答案
```
POST /api/questions/check
Content-Type: application/json

{
  "questionId": 1,
  "answer": "B",
  "userId": 1
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "correct": true,
    "correctAnswer": "B",
    "explanation": "String不是基本数据类型...",
    "referenceAnswer": "详细解答...",
    "score": 100.0
  }
}
```

#### 2.8 创建题目（教师权限）
```
POST /api/questions
Content-Type: application/json

{
  "title": "题目标题",
  "content": "题目内容",
  "type": "CHOICE",
  "difficulty": "MEDIUM",
  "category": "Java",
  "subCategory": "集合框架",
  "tags": "Java,集合",
  "optionA": "选项A",
  "optionB": "选项B",
  "optionC": "选项C",
  "optionD": "选项D",
  "correctAnswer": "A",
  "explanation": "答案解析",
  "referenceAnswer": "参考答案",
  "keyPoints": "知识点",
  "createdBy": 2
}
```

#### 2.9 更新题目
```
PUT /api/questions/{id}
Content-Type: application/json

{题目数据}
```

#### 2.10 删除题目
```
DELETE /api/questions/{id}
```

#### 2.11 增加浏览次数
```
POST /api/questions/{id}/view
```

---

### 3. 面试模块 (InterviewController)

#### 3.1 开始面试
```
POST /api/interview/start
Content-Type: application/json

{
  "userId": 1,
  "sessionType": "TECHNICAL",
  "position": "Java开发工程师"
}

sessionType可选值:
- TECHNICAL: 技术面试
- BEHAVIORAL: 行为面试
- HR: HR面试

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "sessionType": "TECHNICAL",
    "position": "Java开发工程师",
    "status": "ONGOING",
    "conversation": "[{\"role\":\"AI\",\"message\":\"您好！欢迎参加Java开发工程师岗位的技术面试...\",\"timestamp\":\"...\"}]",
    "startTime": "2025-01-01T10:00:00",
    ...
  }
}
```

#### 3.2 AI对话
```
POST /api/interview/chat
Content-Type: application/json

{
  "sessionId": 1,
  "message": "我叫张三，有3年Java开发经验..."
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "aiReply": "很好！请解释一下Java中的多态性是如何实现的？",
    "questionCount": 2
  }
}
```

#### 3.3 结束面试
```
POST /api/interview/end/{sessionId}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "status": "COMPLETED",
    "endTime": "2025-01-01T10:30:00",
    "duration": 1800,
    "totalScore": 85.5,
    "feedback": "本次技术面试表现良好。优点：回答较为完整...",
    ...
  }
}
```

#### 3.4 获取面试历史
```
GET /api/interview/history/{userId}

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "sessionType": "TECHNICAL",
      "position": "Java开发工程师",
      "totalScore": 85.5,
      "status": "COMPLETED",
      "createdAt": "2025-01-01T10:00:00",
      ...
    }
  ]
}
```

#### 3.5 获取面试详情
```
GET /api/interview/session/{sessionId}

返回完整的面试会话信息，包括对话记录
```

---

### 4. 教程模块 (TutorialController)

#### 4.1 获取所有教程
```
GET /api/tutorials
```

#### 4.2 获取教程详情
```
GET /api/tutorials/{id}
```

#### 4.3 按分类获取教程
```
GET /api/tutorials/category/{category}

示例: GET /api/tutorials/category/技术面试
```

#### 4.4 创建教程（教师权限）
```
POST /api/tutorials
Content-Type: application/json

{
  "title": "教程标题",
  "content": "教程内容（支持Markdown）",
  "category": "基础指导",
  "createdBy": 2
}
```

#### 4.5 更新教程
```
PUT /api/tutorials/{id}
```

#### 4.6 删除教程
```
DELETE /api/tutorials/{id}
```

---

### 5. 测试记录模块 (TestRecordController)

#### 5.1 获取用户测试记录
```
GET /api/test-records/user/{userId}
```

#### 5.2 提交测试记录
```
POST /api/test-records
Content-Type: application/json

{
  "userId": 1,
  "category": "Java",
  "score": 85.0,
  "totalQuestions": 10,
  "correctAnswers": 8,
  "timeSpent": 600
}
```

#### 5.3 获取统计信息
```
GET /api/test-records/statistics/{userId}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "totalTests": 10,
    "averageScore": 82.5,
    "highestScore": 95.0,
    "totalQuestions": 100,
    "totalCorrect": 83
  }
}
```

---

## 数据模型

### 用户实体 (User)
```java
- id: Long                      // 用户ID
- username: String              // 用户名
- password: String              // 密码
- email: String                 // 邮箱
- phone: String                 // 手机号
- avatar: String                // 头像URL
- nickname: String              // 昵称
- role: Role                    // 角色(STUDENT/TEACHER/ADMIN)
- membershipType: MembershipType // 会员类型
- targetPosition: String        // 目标岗位
- points: Integer               // 积分
- createdAt: LocalDateTime      // 创建时间
- updatedAt: LocalDateTime      // 更新时间
```

### 题目实体 (Question)
```java
- id: Long                      // 题目ID
- title: String                 // 题目标题
- content: String               // 题目内容
- type: QuestionType            // 题型
- difficulty: Difficulty        // 难度
- category: String              // 一级分类
- subCategory: String           // 二级分类
- tags: String                  // 标签
- optionA-D: String             // 选项(选择题)
- correctAnswer: String         // 正确答案
- explanation: String           // 答案解析
- referenceAnswer: String       // 参考答案
- keyPoints: String             // 知识点
- viewCount: Integer            // 浏览次数
- attemptCount: Integer         // 尝试次数
- passCount: Integer            // 通过次数
- passRate: Double              // 通过率
- createdBy: Long               // 创建者
- createdAt: LocalDateTime      // 创建时间
```

### 面试会话 (InterviewSession)
```java
- id: Long                      // 会话ID
- userId: Long                  // 用户ID
- sessionType: SessionType      // 面试类型
- position: String              // 应聘岗位
- status: Status                // 状态
- startTime: LocalDateTime      // 开始时间
- endTime: LocalDateTime        // 结束时间
- duration: Integer             // 时长(秒)
- totalScore: Double            // 总分
- conversation: String          // 对话记录(JSON)
- feedback: String              // 反馈
```

### 用户能力模型 (UserAbilityModel)
```java
- userId: Long                  // 用户ID
- algorithmAbility: Double      // 算法能力
- codingAbility: Double         // 编码能力
- systemDesignAbility: Double   // 系统设计
- databaseAbility: Double       // 数据库
- networkAbility: Double        // 计算机网络
- osAbility: Double             // 操作系统
- communicationAbility: Double  // 沟通能力
- problemSolvingAbility: Double // 问题解决
- overallScore: Double          // 综合评分
- level: Level                  // 等级
```

---

## 枚举类型

### QuestionType (题型)
- CHOICE: 单选题
- MULTIPLE_CHOICE: 多选题
- CODING: 编程题
- SHORT_ANSWER: 简答题
- SYSTEM_DESIGN: 系统设计题

### Difficulty (难度)
- EASY: 简单
- MEDIUM: 中等
- HARD: 困难

### SessionType (面试类型)
- TECHNICAL: 技术面试
- BEHAVIORAL: 行为面试
- HR: HR面试

### Role (用户角色)
- STUDENT: 学生
- TEACHER: 教师
- ADMIN: 管理员

---

## 初始数据

系统启动时会自动初始化以下数据：

### 用户
- student / 123456 (学生账号)
- teacher / 123456 (教师账号)

### 教程
- 3篇面试指导教程

### 题目
- 8道示例题目(涵盖Java、前端、数据库、算法、系统设计)

---

## 运行说明

1. 确保JDK 1.8+已安装
2. 进入backend目录
3. 运行: `mvn spring-boot:run` 或直接运行主类 `InterviewSystemApplication`
4. 访问: http://localhost:8081

### H2控制台
- URL: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:interview_db
- 用户名: sa
- 密码: (空)

---

## 后续扩展计划

- [ ] 接入真实AI (ChatGPT/文心一言)
- [ ] 实现推荐算法
- [ ] 数据分析模块
- [ ] 社区功能
- [ ] 简历管理
- [ ] Redis缓存
- [ ] 消息队列
