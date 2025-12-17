package com.interview.service;

import com.interview.entity.TestRecord;
import com.interview.repository.TestRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试记录服务
 */
@Service
@Transactional
public class TestRecordService {

    @Autowired
    private TestRecordRepository testRecordRepository;

    /**
     * 获取用户的所有测试记录
     */
    public List<TestRecord> getUserTestRecords(Long userId) {
        return testRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 根据用户ID和分类获取测试记录
     */
    public List<TestRecord> getUserTestRecordsByCategory(Long userId, String category) {
        return testRecordRepository.findByUserIdAndCategory(userId, category);
    }

    /**
     * 提交测试记录
     */
    public TestRecord submitTestRecord(TestRecord testRecord) {
        return testRecordRepository.save(testRecord);
    }

    /**
     * 根据ID获取测试记录
     */
    public Optional<TestRecord> getTestRecordById(Long id) {
        return testRecordRepository.findById(id);
    }

    /**
     * 获取用户的测试统计数据
     */
    public Map<String, Object> getUserStatistics(Long userId) {
        List<TestRecord> records = testRecordRepository.findByUserId(userId);

        Map<String, Object> statistics = new HashMap<>();

        if (records.isEmpty()) {
            statistics.put("totalTests", 0);
            statistics.put("averageScore", 0.0);
            statistics.put("highestScore", 0.0);
            statistics.put("totalQuestions", 0);
            statistics.put("totalCorrect", 0);
            statistics.put("overallAccuracy", 0.0);
            return statistics;
        }

        // 总测试次数
        statistics.put("totalTests", records.size());

        // 平均分
        double avgScore = records.stream()
                .mapToDouble(TestRecord::getScore)
                .average()
                .orElse(0.0);
        statistics.put("averageScore", Math.round(avgScore * 100.0) / 100.0);

        // 最高分
        double highestScore = records.stream()
                .mapToDouble(TestRecord::getScore)
                .max()
                .orElse(0.0);
        statistics.put("highestScore", highestScore);

        // 总题目数
        int totalQuestions = records.stream()
                .mapToInt(TestRecord::getTotalQuestions)
                .sum();
        statistics.put("totalQuestions", totalQuestions);

        // 总正确数
        int totalCorrect = records.stream()
                .mapToInt(TestRecord::getCorrectAnswers)
                .sum();
        statistics.put("totalCorrect", totalCorrect);

        // 总正确率
        double overallAccuracy = totalQuestions > 0
                ? ((double) totalCorrect / totalQuestions * 100)
                : 0.0;
        statistics.put("overallAccuracy", Math.round(overallAccuracy * 100.0) / 100.0);

        // 按分类统计
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Double> categoryAvgScore = new HashMap<>();

        records.forEach(record -> {
            String category = record.getCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        });

        categoryCount.forEach((category, count) -> {
            double avg = records.stream()
                    .filter(r -> category.equals(r.getCategory()))
                    .mapToDouble(TestRecord::getScore)
                    .average()
                    .orElse(0.0);
            categoryAvgScore.put(category, Math.round(avg * 100.0) / 100.0);
        });

        statistics.put("categoryCount", categoryCount);
        statistics.put("categoryAvgScore", categoryAvgScore);

        return statistics;
    }

    /**
     * 获取用户最近的测试记录
     */
    public List<TestRecord> getRecentTestRecords(Long userId, int limit) {
        return testRecordRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * 删除测试记录
     */
    public boolean deleteTestRecord(Long id) {
        if (testRecordRepository.existsById(id)) {
            testRecordRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 获取用户在特定分类的平均分
     */
    public Double getAverageScoreByCategory(Long userId, String category) {
        List<TestRecord> records = testRecordRepository.findByUserIdAndCategory(userId, category);
        if (records.isEmpty()) {
            return 0.0;
        }
        return records.stream()
                .mapToDouble(TestRecord::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * 获取用户总练习题数
     */
    public int getTotalQuestionsAttempted(Long userId) {
        return testRecordRepository.findByUserId(userId).stream()
                .mapToInt(TestRecord::getTotalQuestions)
                .sum();
    }

    /**
     * 获取用户总正确题数
     */
    public int getTotalCorrectAnswers(Long userId) {
        return testRecordRepository.findByUserId(userId).stream()
                .mapToInt(TestRecord::getCorrectAnswers)
                .sum();
    }
}