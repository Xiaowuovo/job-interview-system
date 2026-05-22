package com.interview.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.common.Result;
import com.interview.entity.InterviewSession;
import com.interview.entity.InterviewSession.SessionType;
import com.interview.repository.InterviewSessionRepository;
import com.interview.service.InterviewService;
import com.interview.service.ZhipuAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
@Validated
public class InterviewController {

    private final InterviewService interviewService;
    private final ZhipuAIService zhipuAIService;
    private final InterviewSessionRepository interviewSessionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 开始面试
     */
    @PostMapping("/start")
    public Result<InterviewSession> startInterview(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        // sessionType可选，默认为TECHNICAL
        String sessionTypeStr = request.getOrDefault("sessionType", "TECHNICAL").toString();
        String position = request.get("position").toString();

        SessionType sessionType = SessionType.valueOf(sessionTypeStr);

        InterviewSession session = interviewService.startInterview(userId, sessionType, position);
        return Result.success(session);
    }

    /**
     * AI对话
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        String message = request.get("message").toString();

        Map<String, Object> response = interviewService.chat(sessionId, message);
        return Result.success(response);
    }

    /**
     * 结束面试
     */
    @PostMapping("/end/{sessionId}")
    public Result<InterviewSession> endInterview(@PathVariable Long sessionId) {
        InterviewSession session = interviewService.endInterview(sessionId);
        return Result.success(session);
    }

    /**
     * 获取面试历史
     */
    @GetMapping("/history/{userId}")
    public Result<List<InterviewSession>> getHistory(@PathVariable Long userId) {
        return Result.success(interviewService.getUserInterviewHistory(userId));
    }

    /**
     * 获取面试详情
     */
    @GetMapping("/session/{sessionId}")
    public Result<InterviewSession> getSession(@PathVariable Long sessionId) {
        return interviewService.getInterviewSession(sessionId)
                .map(Result::success)
                .orElse(Result.error("面试会话不存在"));
    }

    /**
     * AI 面试报告：基于对话记录打分+总结
     */
    @GetMapping("/ai-report/{sessionId}")
    public Result<Map<String, Object>> getAiReport(@PathVariable Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("面试会话不存在"));

        String conversation = session.getConversation();
        if (conversation == null || conversation.equals("[]")) {
            return Result.error("暂无对话内容，无法生成报告");
        }

        // 将对话 JSON 转换为易读文本
        StringBuilder text = new StringBuilder();
        try {
            List<Map<String, String>> msgs = objectMapper.readValue(
                    conversation, new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> m : msgs) {
                String role = "AI".equals(m.get("role")) ? "面试官" : "候选人";
                text.append(role).append("：").append(m.get("message")).append("\n");
            }
        } catch (Exception e) {
            return Result.error("对话记录解析失败");
        }

        String reportContent = zhipuAIService.generateInterviewReport(
                session.getPosition() != null ? session.getPosition() : "未知岗位",
                text.toString()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("position", session.getPosition());
        result.put("startTime", session.getStartTime());
        result.put("report", reportContent);
        return Result.success(result);
    }
}
