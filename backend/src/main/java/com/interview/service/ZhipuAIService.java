package com.interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 智谱AI服务 - 接入GLM-4-Flash免费模型
 * API文档: https://open.bigmodel.cn/dev/api
 */
@Slf4j
@Service
public class ZhipuAIService {

    // 智谱AI API配置
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String MODEL = "glm-4-flash"; // 免费模型
    
    // API Key - 可在配置文件中设置，这里提供测试用的
    // 用户可以去 https://open.bigmodel.cn/ 免费注册获取自己的API Key
    @Value("${zhipu.api.key:test_key}")
    private String apiKey;
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public ZhipuAIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 调用智谱AI生成回复
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @param conversationHistory 对话历史（可选）
     * @return AI回复内容
     */
    public String generateReply(String systemPrompt, String userMessage, String conversationHistory) {
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", MODEL);
            
            // 构建消息数组
            ArrayNode messages = objectMapper.createArrayNode();
            
            // 添加系统提示
            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
            
            // 添加用户消息
            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            requestBody.set("messages", messages);
            
            // 设置其他参数
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1024);
            
            log.info("调用智谱AI API，模型: {}", MODEL);
            
            // 如果没有配置真实API Key，返回模拟回复
            if (apiKey == null || apiKey.equals("test_key") || apiKey.trim().isEmpty()) {
                log.warn("未配置智谱AI API Key，使用模拟回复模式");
                return generateMockReply(userMessage);
            }
            
            // 构建请求
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("智谱AI API调用失败: {}", response.code());
                    return generateMockReply(userMessage);
                }
                
                String responseBody = response.body().string();
                log.info("智谱AI响应: {}", responseBody);
                
                // 解析响应
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                JsonNode choices = jsonResponse.get("choices");
                
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null) {
                        String content = message.get("content").asText();
                        log.info("AI回复成功");
                        return content;
                    }
                }
                
                log.warn("无法解析AI响应，使用模拟回复");
                return generateMockReply(userMessage);
            }
            
        } catch (IOException e) {
            log.error("调用智谱AI API异常: {}", e.getMessage(), e);
            return generateMockReply(userMessage);
        }
    }
    
    /**
     * 模拟AI回复（当API不可用时使用）
     */
    private String generateMockReply(String userMessage) {
        log.info("使用模拟AI回复模式");
        
        // 简单的规则引擎
        String lowerMsg = userMessage.toLowerCase();
        
        if (lowerMsg.contains("自我介绍") || lowerMsg.contains("介绍一下")) {
            return "很好！您的自我介绍很清晰。那么请问，您能详细说说您最近参与的一个项目吗？包括技术栈、您负责的部分以及遇到的挑战。";
        } else if (lowerMsg.contains("项目") || lowerMsg.contains("开发")) {
            return "听起来很不错！在这个项目中，您是如何解决技术难题的？能举个具体的例子吗？";
        } else if (lowerMsg.contains("java") || lowerMsg.contains("spring")) {
            return "很好！那么请解释一下Spring Boot的核心特性和优势是什么？";
        } else if (lowerMsg.contains("数据库") || lowerMsg.contains("sql")) {
            return "了解了。请问您在数据库优化方面有什么经验？如何处理慢查询问题？";
        } else if (lowerMsg.contains("算法") || lowerMsg.contains("数据结构")) {
            return "好的。那么能否讲讲常见的排序算法以及它们的时间复杂度？";
        } else {
            return "感谢您的回答。接下来，请谈谈您的职业规划，以及您对这个岗位的理解。";
        }
    }
    
    /**
     * 基于对话历史生成面试报告（打分+总结）
     */
    public String generateInterviewReport(String position, String conversationText) {
        String systemPrompt = "你是一位专业的面试评估专家，请根据以下面试对话内容，给候选人做出评估报告。\n" +
            "报告必须包含以下几个部分，用中文回答：\n" +
            "1. 【综合评分】：0-100分，并说明评分依据\n" +
            "2. 【优势亮点】：候选人表现好的方面（3条以内）\n" +
            "3. 【待改进项】：需要加强的地方（3条以内）\n" +
            "4. 【总体总结】：对本次面试的综合评价，100字以内\n" +
            "5. 【改进建议】：给候选人的具体学习和提升建议\n" +
            "请严格按照上述格式输出，不要输出其他内容。";
        String userMessage = String.format("应聘岗位：%s\n\n面试对话记录如下：\n%s", position, conversationText);
        return generateReply(systemPrompt, userMessage, "");
    }

    /**
     * 生成面试系统提示词
     */
    public String generateInterviewSystemPrompt(String position) {
        return String.format(
            "你是一位专业的%s岗位面试官。请根据候选人的回答进行专业的追问和评价。" +
            "你的问题应该具有针对性，既要考察专业技能，也要了解项目经验和解决问题的能力。" +
            "保持友好但专业的态度，每次回复控制在100字以内。" +
            "不要一次问太多问题，每次只问1-2个相关问题。",
            position
        );
    }
}
