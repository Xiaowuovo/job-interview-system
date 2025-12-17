package com.interview.service;

import com.interview.entity.InterviewSession;
import com.interview.entity.InterviewSession.SessionType;
import com.interview.entity.InterviewSession.Status;
import com.interview.repository.InterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 开始面试
     */
    @Transactional
    public InterviewSession startInterview(Long userId, SessionType sessionType, String position) {
        InterviewSession session = new InterviewSession();
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setPosition(position);
        session.setStatus(Status.ONGOING);
        session.setStartTime(LocalDateTime.now());

        // 初始化对话记录
        List<Map<String, String>> conversation = new ArrayList<>();
        Map<String, String> greeting = new HashMap<>();
        greeting.put("role", "AI");
        greeting.put("message", generateGreeting(sessionType, position));
        greeting.put("timestamp", LocalDateTime.now().toString());
        conversation.add(greeting);

        try {
            session.setConversation(objectMapper.writeValueAsString(conversation));
        } catch (Exception e) {
            session.setConversation("[]");
        }

        return interviewSessionRepository.save(session);
    }

    /**
     * 生成开场白
     */
    private String generateGreeting(SessionType type, String position) {
        switch (type) {
            case TECHNICAL:
                return String.format("您好！欢迎参加%s岗位的技术面试。我将围绕技术能力、项目经验和算法问题进行提问。请先简单介绍一下您的技术背景。", position);
            case BEHAVIORAL:
                return String.format("您好！欢迎参加%s岗位的行为面试。我将了解您的工作经历、团队协作和问题解决能力。请用STAR法则分享一个您解决问题的经历。", position);
            case HR:
                return String.format("您好！欢迎参加%s岗位的HR面试。我将了解您的职业规划、期望薪资和个人特质。请先介绍一下您为什么选择这个岗位。", position);
            default:
                return "您好！欢迎参加面试，请开始您的自我介绍。";
        }
    }

    /**
     * AI对话（简化版，实际应接入真实AI）
     */
    @Transactional
    public Map<String, Object> chat(Long sessionId, String userMessage) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("面试会话不存在"));

        if (session.getStatus() != Status.ONGOING) {
            throw new RuntimeException("面试已结束");
        }

        // 解析现有对话
        List<Map<String, String>> conversation;
        try {
            conversation = objectMapper.readValue(
                session.getConversation(),
                new TypeReference<List<Map<String, String>>>() {}
            );
        } catch (Exception e) {
            conversation = new ArrayList<>();
        }

        // 添加用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "USER");
        userMsg.put("message", userMessage);
        userMsg.put("timestamp", LocalDateTime.now().toString());
        conversation.add(userMsg);

        // 生成AI回复（这里是模拟，实际应调用GPT API）
        String aiReply = generateAIReply(session.getSessionType(), userMessage, conversation.size());

        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "AI");
        aiMsg.put("message", aiReply);
        aiMsg.put("timestamp", LocalDateTime.now().toString());
        conversation.add(aiMsg);

        // 保存对话
        try {
            session.setConversation(objectMapper.writeValueAsString(conversation));
        } catch (Exception e) {
            // Handle exception
        }
        interviewSessionRepository.save(session);

        Map<String, Object> response = new HashMap<>();
        response.put("aiReply", aiReply);
        response.put("questionCount", conversation.size() / 2);

        return response;
    }

    /**
     * 生成AI回复（模拟版本）
     */
    private String generateAIReply(SessionType type, String userMessage, int messageCount) {
        // 这里是简化的规则引擎，实际应接入GPT等AI服务
        List<String> technicalQuestions = Arrays.asList(
            "很好！请解释一下Java中的多态性是如何实现的？",
            "能详细说说Spring Boot的自动配置原理吗？",
            "请手写一个单例模式的实现",
            "如何设计一个高并发的系统？请从架构层面分析",
            "面试接近尾声，还有什么问题想问我的吗？"
        );

        List<String> behavioralQuestions = Arrays.asList(
            "很好！请分享一次您在团队中遇到冲突的经历，您是如何解决的？",
            "描述一个您超预期完成的项目，您做了哪些努力？",
            "当您面对紧急的线上问题时，您的处理流程是什么？",
            "请说说您的职业规划，您希望在未来3年达到什么目标？",
            "感谢您的分享！还有什么想补充的吗？"
        );

        int questionIndex = Math.min((messageCount / 2), 4);

        if (type == SessionType.TECHNICAL) {
            return technicalQuestions.get(questionIndex);
        } else if (type == SessionType.BEHAVIORAL) {
            return behavioralQuestions.get(questionIndex);
        } else {
            return "感谢您的回答！您对薪资有什么期望吗？";
        }
    }

    /**
     * 结束面试
     */
    @Transactional
    public InterviewSession endInterview(Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("面试会话不存在"));

        session.setStatus(Status.COMPLETED);
        session.setEndTime(LocalDateTime.now());

        // 计算时长
        Duration duration = Duration.between(session.getStartTime(), session.getEndTime());
        session.setDuration((int) duration.getSeconds());

        // 生成评分和反馈（简化版本）
        Map<String, Object> evaluation = evaluateInterview(session);
        session.setTotalScore((Double) evaluation.get("score"));
        session.setFeedback((String) evaluation.get("feedback"));

        return interviewSessionRepository.save(session);
    }

    /**
     * 评估面试表现
     */
    private Map<String, Object> evaluateInterview(InterviewSession session) {
        Map<String, Object> result = new HashMap<>();

        // 简化评分逻辑（实际应通过AI分析对话内容）
        double score = 70 + Math.random() * 20; // 70-90分

        String feedback = String.format(
            "本次%s面试表现良好。\n" +
            "优点：回答较为完整，思路清晰。\n" +
            "建议：可以进一步深化技术细节，增加实际案例的分享。\n" +
            "总体评分：%.1f分",
            session.getSessionType() == SessionType.TECHNICAL ? "技术" : "行为",
            score
        );

        result.put("score", score);
        result.put("feedback", feedback);

        return result;
    }

    /**
     * 获取用户的面试历史
     */
    public List<InterviewSession> getUserInterviewHistory(Long userId) {
        return interviewSessionRepository.findByUserId(userId);
    }

    /**
     * 获取面试详情
     */
    public Optional<InterviewSession> getInterviewSession(Long sessionId) {
        return interviewSessionRepository.findById(sessionId);
    }
}
