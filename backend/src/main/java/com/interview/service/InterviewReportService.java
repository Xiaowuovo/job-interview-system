package com.interview.service;

import com.interview.entity.*;
import com.interview.repository.InterviewEvaluationRepository;
import com.interview.repository.InterviewReportRepository;
import com.interview.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewReportService {

    @Autowired
    private InterviewReportRepository reportRepository;

    @Autowired
    private InterviewEvaluationRepository evaluationRepository;

    @Autowired
    private InterviewSessionRepository sessionRepository;

    @Transactional
    public InterviewReport generateReport(Long sessionId) {
        if (reportRepository.existsBySessionId(sessionId)) {
            return reportRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("报告不存在"));
        }

        InterviewSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("面试会话不存在"));

        List<InterviewEvaluation> evaluations = evaluationRepository.findBySessionIdOrderByIdAsc(sessionId);
        
        if (evaluations.isEmpty()) {
            throw new RuntimeException("没有可用的评估数据");
        }

        InterviewReport report = new InterviewReport();
        report.setSessionId(sessionId);

        // 计算各项得分
        double totalScore = 0;
        double technicalTotal = 0;
        double communicationTotal = 0;
        double problemSolvingTotal = 0;
        int technicalCount = 0;
        int communicationCount = 0;

        for (InterviewEvaluation eval : evaluations) {
            totalScore += eval.getTotalScore();
            
            // 根据题目类型分类统计
            technicalTotal += eval.getAccuracyScore();
            technicalCount++;
            
            communicationTotal += eval.getExpressionScore();
            communicationCount++;
            
            problemSolvingTotal += eval.getLogicScore();
        }

        // 计算平均分
        double avgScore = totalScore / evaluations.size();
        report.setOverallScore(avgScore);

        // 各维度得分
        report.setTechnicalScore(technicalCount > 0 ? technicalTotal / technicalCount : 0);
        report.setCommunicationScore(communicationCount > 0 ? communicationTotal / communicationCount : 0);
        report.setProblemSolvingScore(problemSolvingTotal / evaluations.size());
        report.setLearningAbilityScore(calculateLearningAbility(evaluations));

        // 排名百分位（简化计算）
        report.setRankingPercentile(calculateRankingPercentile(avgScore));

        // 生成总结
        generateSummary(report, evaluations);

        return reportRepository.save(report);
    }

    public InterviewReport getReportBySessionId(Long sessionId) {
        return reportRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("面试报告不存在"));
    }

    private double calculateLearningAbility(List<InterviewEvaluation> evaluations) {
        if (evaluations.size() < 2) return 50.0;
        
        double firstScore = evaluations.get(0).getTotalScore();
        double lastScore = evaluations.get(evaluations.size() - 1).getTotalScore();
        
        double improvement = lastScore - firstScore;
        return Math.min(50.0 + improvement, 100.0);
    }

    private double calculateRankingPercentile(double score) {
        if (score >= 90) return 95.0;
        if (score >= 80) return 85.0;
        if (score >= 70) return 70.0;
        if (score >= 60) return 50.0;
        return 30.0;
    }

    private void generateSummary(InterviewReport report, List<InterviewEvaluation> evaluations) {
        double overallScore = report.getOverallScore();

        // 优势总结
        StringBuilder strengths = new StringBuilder();
        if (report.getTechnicalScore() >= 30) {
            strengths.append("技术能力扎实；");
        }
        if (report.getCommunicationScore() >= 7) {
            strengths.append("表达清晰流畅；");
        }
        if (report.getProblemSolvingScore() >= 15) {
            strengths.append("逻辑思维能力强；");
        }
        report.setStrengthsSummary(strengths.length() > 0 ? strengths.toString() : "基础掌握良好");

        // 劣势总结
        StringBuilder weaknesses = new StringBuilder();
        if (report.getTechnicalScore() < 20) {
            weaknesses.append("技术深度有待提高；");
        }
        if (report.getCommunicationScore() < 5) {
            weaknesses.append("表达能力需要加强；");
        }
        if (report.getProblemSolvingScore() < 10) {
            weaknesses.append("逻辑分析能力较弱；");
        }
        report.setWeaknessesSummary(weaknesses.length() > 0 ? weaknesses.toString() : "继续保持学习态度");

        // 提升计划
        StringBuilder plan = new StringBuilder();
        if (overallScore < 60) {
            plan.append("1. 系统学习基础知识，巩固技术栈；\n");
            plan.append("2. 多做面试题练习，熟悉常见问题；\n");
            plan.append("3. 参加模拟面试，积累实战经验。");
        } else if (overallScore < 80) {
            plan.append("1. 深入学习高级技术，提升技术深度；\n");
            plan.append("2. 练习项目讲解，提高表达能力；\n");
            plan.append("3. 研究算法和系统设计，增强问题解决能力。");
        } else {
            plan.append("1. 关注前沿技术，保持技术敏感度；\n");
            plan.append("2. 总结面试经验，形成个人方法论；\n");
            plan.append("3. 准备深度问题，应对资深岗位面试。");
        }
        report.setImprovementPlan(plan.toString());

        // 推荐资源
        report.setRecommendedResources("[\"Java核心技术\", \"算法导论\", \"系统设计面试\"]");

        // 对标数据（模拟）
        report.setSimilarUsersAvgScore(65.0);
        report.setTargetCompanyAvgScore(75.0);
    }
}
