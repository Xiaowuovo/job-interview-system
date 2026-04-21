# AI模拟问答功能 - 智谱AI接入说明

## 功能说明
本系统已成功接入**智谱AI（GLM-4-Flash）**国产免费大模型，实现真正的AI智能问答功能。

## 智谱AI介绍
- **模型**: GLM-4-Flash
- **特点**: 国产大模型，免费且稳定
- **免费额度**: 每天100万tokens
- **官网**: https://open.bigmodel.cn/

## 配置步骤

### 1. 注册智谱AI账号（免费）
1. 访问 https://open.bigmodel.cn/
2. 点击"开始使用"注册账号
3. 登录后进入控制台

### 2. 获取API Key
1. 在控制台左侧菜单点击"API密钥"
2. 点击"创建新的API密钥"
3. 复制生成的API Key（以 `xxx.xxx` 格式）

### 3. 配置到系统中

#### 方式一：环境变量（推荐）
```bash
# Windows
set ZHIPU_API_KEY=your_api_key_here

# Linux/Mac
export ZHIPU_API_KEY=your_api_key_here
```

#### 方式二：直接配置文件
编辑 `backend/src/main/resources/application.yml`：
```yaml
zhipu:
  api:
    key: your_api_key_here  # 替换为您的真实API Key
```

### 4. 重启后端服务
配置完成后，重启Spring Boot应用即可生效。

## 使用说明

### 模拟AI模式（默认）
- 如果**未配置API Key**，系统将自动使用模拟AI回复模式
- 模拟模式下使用简单的规则引擎生成回复
- 不会消耗智谱AI额度，但回复质量较低

### 真实AI模式
- 配置API Key后，系统将调用智谱AI的GLM-4-Flash模型
- 回复更智能、更自然、更专业
- 每次对话约消耗100-500 tokens
- 免费额度可支持每天2000-10000轮对话

## 测试方法

### 1. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 2. 查看日志
启动后查看控制台日志：
- 如果看到 `未配置智谱AI API Key，使用模拟回复模式` - 表示使用模拟模式
- 如果看到 `调用智谱AI API，模型: glm-4-flash` - 表示真实AI接入成功

### 3. 测试AI问答
1. 访问前端页面
2. 点击"AI模拟问答"
3. 选择岗位，开始问答
4. 输入您的回答，查看AI的回复质量

## 特点优势

✅ **真实AI接入**: 使用智谱AI GLM-4-Flash模型  
✅ **完全免费**: 每天100万tokens免费额度  
✅ **国产模型**: 支持国产AI发展  
✅ **智能回复**: 根据对话上下文生成专业回复  
✅ **降级方案**: 未配置时自动降级为模拟模式  
✅ **易于配置**: 仅需一个API Key即可使用  

## 技术实现

- **HTTP客户端**: OkHttp 4.11.0
- **AI服务**: ZhipuAIService.java
- **API地址**: https://open.bigmodel.cn/api/paas/v4/chat/completions
- **系统提示词**: 针对不同岗位生成专业的面试官角色
- **上下文管理**: 保留最近10轮对话作为上下文

## 常见问题

### Q1: 如何知道AI是否真正接入成功？
A: 查看后端日志，如果显示"调用智谱AI API"则表示成功接入。

### Q2: API Key配置错误会怎样？
A: 系统会自动降级为模拟AI模式，不会报错。

### Q3: 免费额度用完了怎么办？
A: 可以升级为付费版本，或等待第二天额度刷新。

### Q4: 回复速度慢怎么办？
A: GLM-4-Flash模型已经是最快的，通常2-5秒内返回。如果很慢，检查网络连接。

### Q5: 可以换其他AI模型吗？
A: 可以！在ZhipuAIService.java中修改MODEL常量，智谱AI支持多个模型。

## 后续扩展

可以考虑接入更多国产AI：
- 百度文心一言
- 阿里通义千问
- 讯飞星火
- DeepSeek（完全开源免费）

---

**注意**: 请妥善保管您的API Key，不要泄露或上传到公开代码库！
