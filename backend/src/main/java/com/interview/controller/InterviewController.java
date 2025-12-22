package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.InterviewSession;
import com.interview.entity.InterviewSession.SessionType;
import com.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
@Validated
public class InterviewController {

    private final InterviewService interviewService;

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
}
