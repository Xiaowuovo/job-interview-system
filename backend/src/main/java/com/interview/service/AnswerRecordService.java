package com.interview.service;

import com.interview.entity.AnswerRecord;
import com.interview.entity.UserAbilityModel;
import com.interview.repository.AnswerRecordRepository;
import com.interview.repository.UserAbilityModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 答题记录服务
 */
@Service
@Transactional
public class AnswerRecordService {

    @Autowired
    private AnswerRecordRepository answerRecordRepository;

    @Autowired
    private UserAbilityModelRepository abilityModelRepository;

    /**
     * 保存答题记录
     */
    public AnswerRecord saveAnswerRecord(AnswerRecord record) {
        AnswerRecord saved = answerRecordRepository.save(record);

        // 更新用户能力模型
        updateUserAbility(record.getUserId(), record);

        return saved;
    }

    /**
     * 获取用户的答题记录
     */
    public List<AnswerRecord> getUserAnswerRecords(Long userId) {
        return answerRecordRepository.findByUserId(userId);
    }

    /**
     * 获取用户对某题的答题记录
     */
    public List<AnswerRecord> getUserQuestionRecords(Long userId, Long questionId) {
        return answerRecordRepository.findByUserIdAndQuestionId(userId, questionId);
    }

    /**
     * 获取用户最近的答题记录
     */
    public List<AnswerRecord> getRecentAnswerRecords(Long userId, int limit) {
        return answerRecordRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * 获取用户答题统计
     */
    public Map<String, Object> getUserAnswerStatistics(Long userId) {
        List<AnswerRecord> records = answerRecordRepository.findByUserId(userId);

        Map<String, Object> statistics = new HashMap<>();

        if (records.isEmpty()) {
            statistics.put("totalAttempts", 0);
            statistics.put("correctCount", 0);
            statistics.put("wrongCount", 0);
            statistics.put("accuracy", 0.0);
            statistics.put("averageScore", 0.0);
            statistics.put("totalTimeSpent", 0);
            return statistics;
        }

        // 总答题次数
        statistics.put("totalAttempts", records.size());

        // 正确次数
        long correctCount = records.stream()
                .filter(AnswerRecord::getIsCorrect)
                .count();
        statistics.put("correctCount", correctCount);

        // 错误次数
        long wrongCount = records.size() - correctCount;
        statistics.put("wrongCount", wrongCount);

        // 正确率
        double accuracy = (double) correctCount / records.size() * 100;
        statistics.put("accuracy", Math.round(accuracy * 100.0) / 100.0);

        // 平均分
        double avgScore = records.stream()
                .filter(r -> r.getScore() != null)
                .mapToDouble(AnswerRecord::getScore)
                .average()
                .orElse(0.0);
        statistics.put("averageScore", Math.round(avgScore * 100.0) / 100.0);

        // 总用时(秒)
        int totalTimeSpent = records.stream()
                .filter(r -> r.getTimeSpent() != null)
                .mapToInt(AnswerRecord::getTimeSpent)
                .sum();
        statistics.put("totalTimeSpent", totalTimeSpent);

        // 平均用时(秒)
        double avgTimeSpent = records.stream()
                .filter(r -> r.getTimeSpent() != null)
                .mapToInt(AnswerRecord::getTimeSpent)
                .average()
                .orElse(0.0);
        statistics.put("averageTimeSpent", Math.round(avgTimeSpent * 100.0) / 100.0);

        return statistics;
    }

    /**
     * 检查用户是否已做过某题
     */
    public boolean hasUserAnsweredQuestion(Long userId, Long questionId) {
        List<AnswerRecord> records = answerRecordRepository
                .findByUserIdAndQuestionId(userId, questionId);
        return !records.isEmpty();
    }

    /**
     * 获取用户对某题的最佳成绩
     */
    public Optional<AnswerRecord> getBestScoreRecord(Long userId, Long questionId) {
        List<AnswerRecord> records = answerRecordRepository
                .findByUserIdAndQuestionId(userId, questionId);

        return records.stream()
                .filter(r -> r.getScore() != null)
                .max((a, b) -> Double.compare(a.getScore(), b.getScore()));
    }

    /**
     * 更新用户能力模型(根据答题记录)
     */
    private void updateUserAbility(Long userId, AnswerRecord record) {
        Optional<UserAbilityModel> abilityOpt = abilityModelRepository.findByUserId(userId);

        if (abilityOpt.isEmpty()) {
            return; // 能力模型不存在，跳过
        }

        UserAbilityModel ability = abilityOpt.get();

        // 简单的能力模型更新逻辑
        // 根据题目类型和答题结果调整能力值
        double adjustment = record.getIsCorrect() ? 1.0 : -0.5;
        double currentScore = record.getScore() != null ? record.getScore() :
                             (record.getIsCorrect() ? 100.0 : 0.0);

        // 这里可以根据题目的标签、分类等更精确地更新对应的能力维度
        // 示例：简单地提升编码能力
        double newCodingAbility = ability.getCodingAbility() + (adjustment * 0.1);
        newCodingAbility = Math.max(0, Math.min(100, newCodingAbility)); // 限制在0-100
        ability.setCodingAbility(newCodingAbility);

        // 更新综合评分
        updateOverallScore(ability);

        ability.setUpdatedAt(LocalDateTime.now());
        abilityModelRepository.save(ability);
    }

    /**
     * 更新综合评分
     */
    private void updateOverallScore(UserAbilityModel ability) {
        double overall = (
                ability.getAlgorithmAbility() +
                ability.getCodingAbility() +
                ability.getSystemDesignAbility() +
                ability.getDatabaseAbility() +
                ability.getNetworkAbility() +
                ability.getOsAbility() +
                ability.getCommunicationAbility() +
                ability.getProblemSolvingAbility()
        ) / 8.0;

        ability.setOverallScore(overall);

        // 更新等级
        if (overall >= 85) {
            ability.setLevel(UserAbilityModel.Level.EXPERT);
        } else if (overall >= 70) {
            ability.setLevel(UserAbilityModel.Level.ADVANCED);
        } else if (overall >= 50) {
            ability.setLevel(UserAbilityModel.Level.INTERMEDIATE);
        } else {
            ability.setLevel(UserAbilityModel.Level.BEGINNER);
        }
    }

    /**
     * 删除答题记录
     */
    public boolean deleteAnswerRecord(Long id) {
        if (answerRecordRepository.existsById(id)) {
            answerRecordRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
