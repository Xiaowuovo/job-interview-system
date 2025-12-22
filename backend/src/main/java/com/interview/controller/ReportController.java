package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.*;
import com.interview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习报告控制器
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final StudyRecordRepository studyRecordRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final TestRecordRepository testRecordRepository;
    private final InterviewSessionRepository interviewRepository;
    private final WrongQuestionBookRepository wrongQuestionRepository;
    private final UserAbilityModelRepository abilityModelRepository;
    private final QuestionRepository questionRepository;

    /**
     * 生成学习报告
     */
    @GetMapping("/study/{userId}")
    public Result<Map<String, Object>> generateStudyReport(
            @PathVariable Long userId,
            @RequestParam(required = false) String period) {

        Map<String, Object> report = new HashMap<>();

        // 确定时间范围
        LocalDateTime startTime;
        LocalDateTime endTime = LocalDateTime.now();
        String periodName;

        if ("week".equals(period)) {
            startTime = endTime.minusWeeks(1);
            periodName = "本周";
        } else if ("month".equals(period)) {
            startTime = endTime.minusMonths(1);
            periodName = "本月";
        } else {
            // 默认全部
            startTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            periodName = "累计";
        }

        report.put("period", periodName);
        report.put("startDate", startTime);
        report.put("endDate", endTime);

        // 1. 学习时长统计
        List<StudyRecord> studyRecords = studyRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        int totalStudyTime = studyRecords.stream()
                .mapToInt(StudyRecord::getDuration)
                .sum();

        long studyDays = studyRecords.stream()
                .map(r -> r.getCreatedAt().toLocalDate())
                .distinct()
                .count();

        report.put("totalStudyTime", totalStudyTime);
        report.put("studyDays", studyDays);
        report.put("avgStudyTime", studyDays > 0 ? totalStudyTime / studyDays : 0);

        // 2. 刷题统计
        List<AnswerRecord> answerRecords = answerRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        long totalQuestions = answerRecords.size();
        long correctQuestions = answerRecords.stream()
                .filter(AnswerRecord::getIsCorrect)
                .count();

        double accuracy = totalQuestions > 0 ? (correctQuestions * 100.0 / totalQuestions) : 0;

        report.put("totalQuestions", totalQuestions);
        report.put("correctQuestions", correctQuestions);
        report.put("accuracy", String.format("%.1f", accuracy));

        // 3. 分类正确率统计
        Map<String, Map<String, Object>> categoryStats = answerRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> {
                            return questionRepository.findById(record.getQuestionId())
                                    .map(Question::getCategory)
                                    .orElse("未知");
                        },
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long total = list.size();
                                    long correct = list.stream()
                                            .filter(AnswerRecord::getIsCorrect)
                                            .count();
                                    Map<String, Object> stat = new HashMap<>();
                                    stat.put("total", total);
                                    stat.put("correct", correct);
                                    stat.put("accuracy", total > 0 ? (correct * 100.0 / total) : 0);
                                    return stat;
                                }
                        )
                ));

        report.put("categoryStats", categoryStats);

        // 4. 测试统计
        List<TestRecord> testRecords = testRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        long testCount = testRecords.size();
        double avgTestScore = testRecords.stream()
                .mapToDouble(TestRecord::getScore)
                .average()
                .orElse(0);

        report.put("testCount", testCount);
        report.put("avgTestScore", String.format("%.1f", avgTestScore));

        // 5. 面试统计
        List<InterviewSession> interviews = interviewRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        long interviewCount = interviews.size();
        double avgInterviewScore = interviews.stream()
                .filter(i -> InterviewSession.Status.COMPLETED.equals(i.getStatus()))
                .filter(i -> i.getAvgScore() != null)
                .mapToDouble(InterviewSession::getAvgScore)
                .average()
                .orElse(0);

        report.put("interviewCount", interviewCount);
        report.put("avgInterviewScore", String.format("%.1f", avgInterviewScore));

        // 6. 错题本统计
        List<WrongQuestionBook> wrongQuestions = wrongQuestionRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        long wrongCount = wrongQuestions.size();
        long masteredCount = wrongQuestions.stream()
                .filter(WrongQuestionBook::getIsMastered)
                .count();

        report.put("wrongCount", wrongCount);
        report.put("masteredCount", masteredCount);

        // 7. 能力模型
        UserAbilityModel ability = abilityModelRepository.findByUserId(userId).orElse(null);
        report.put("abilityModel", ability);

        // 8. 学习建议
        List<String> suggestions = generateSuggestions(
                totalStudyTime, totalQuestions, accuracy, avgTestScore,
                avgInterviewScore, categoryStats);
        report.put("suggestions", suggestions);

        // 9. 成就徽章（本期新获得的）
        List<String> achievements = new ArrayList<>();
        if (studyDays >= 7) achievements.add("坚持学习7天");
        if (totalQuestions >= 100) achievements.add("刷题达人");
        if (accuracy >= 90) achievements.add("正确率之王");
        if (testCount >= 5) achievements.add("测试狂魔");
        if (interviewCount >= 3) achievements.add("面试高手");

        report.put("achievements", achievements);

        return Result.success(report);
    }

    /**
     * 生成学习建议
     */
    private List<String> generateSuggestions(
            int totalStudyTime, long totalQuestions, double accuracy,
            double avgTestScore, double avgInterviewScore,
            Map<String, Map<String, Object>> categoryStats) {

        List<String> suggestions = new ArrayList<>();

        // 学习时长建议
        if (totalStudyTime < 300) {
            suggestions.add("学习时长较少，建议每天至少学习1小时");
        } else if (totalStudyTime > 1000) {
            suggestions.add("学习时长充足，继续保持！");
        }

        // 刷题建议
        if (totalQuestions < 50) {
            suggestions.add("刷题数量较少，建议增加练习量");
        } else if (accuracy < 70) {
            suggestions.add("正确率偏低，建议先巩固基础知识后再刷题");
        } else if (accuracy > 85) {
            suggestions.add("正确率优秀！可以尝试更难的题目");
        }

        // 分类建议
        for (Map.Entry<String, Map<String, Object>> entry : categoryStats.entrySet()) {
            String category = entry.getKey();
            Map<String, Object> stat = entry.getValue();
            double catAccuracy = (Double) stat.get("accuracy");

            if (catAccuracy < 60) {
                suggestions.add(String.format("%s类题目薄弱，建议重点复习", category));
            }
        }

        // 测试建议
        if (avgTestScore < 60) {
            suggestions.add("测试成绩偏低，建议系统复习知识点");
        }

        // 面试建议
        if (avgInterviewScore < 70) {
            suggestions.add("面试表现需要提升，多进行模拟练习");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("学习进度良好，继续保持！");
        }

        return suggestions;
    }

    /**
     * 获取学习趋势数据（用于成长曲线）
     */
    @GetMapping("/trend/{userId}")
    public Result<Map<String, Object>> getStudyTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {

        Map<String, Object> trend = new HashMap<>();

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);

        // 按日期统计学习时长
        List<StudyRecord> records = studyRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        Map<LocalDate, Integer> dailyStudyTime = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.summingInt(StudyRecord::getDuration)
                ));

        // 按日期统计刷题数量
        List<AnswerRecord> answers = answerRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, startTime, endTime);

        Map<LocalDate, Long> dailyQuestionCount = answers.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // 按日期统计正确率
        Map<LocalDate, Double> dailyAccuracy = answers.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long total = list.size();
                                    long correct = list.stream()
                                            .filter(AnswerRecord::getIsCorrect)
                                            .count();
                                    return total > 0 ? (correct * 100.0 / total) : 0;
                                }
                        )
                ));

        // 构建完整的日期序列
        List<String> dates = new ArrayList<>();
        List<Integer> studyTimes = new ArrayList<>();
        List<Long> questionCounts = new ArrayList<>();
        List<Double> accuracies = new ArrayList<>();

        LocalDate current = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        while (!current.isAfter(end)) {
            dates.add(current.toString());
            studyTimes.add(dailyStudyTime.getOrDefault(current, 0));
            questionCounts.add(dailyQuestionCount.getOrDefault(current, 0L));
            accuracies.add(dailyAccuracy.getOrDefault(current, 0.0));

            current = current.plusDays(1);
        }

        trend.put("dates", dates);
        trend.put("studyTimes", studyTimes);
        trend.put("questionCounts", questionCounts);
        trend.put("accuracies", accuracies);

        return Result.success(trend);
    }
}
