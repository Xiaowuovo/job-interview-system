package com.interview.service;

import com.interview.entity.PointsRecord;
import com.interview.entity.User;
import com.interview.repository.PointsRecordRepository;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分服务
 */
@Service
@Transactional
public class PointsService {

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 积分规则配置
     */
    private static final Map<String, Integer> POINTS_RULES = new HashMap<String, Integer>() {{
        put("DAILY_SIGNIN", 10);      // 每日签到
        put("STUDY", 5);               // 学习知识点
        put("PRACTICE_CORRECT", 3);    // 答题正确
        put("PRACTICE_WRONG", 1);      // 答题错误
        put("TEST_PASS", 20);          // 测试通过
        put("INTERVIEW_GOOD", 30);     // 面试良好
        put("SHARE", 15);              // 分享内容
        put("CONTINUOUS_SIGNIN_7", 50); // 连续签到7天
    }};

    /**
     * 添加积分
     */
    public PointsRecord addPoints(Long userId, String type, String description) {
        Integer points = POINTS_RULES.getOrDefault(type, 0);
        if (points == 0) {
            return null;
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);

        // 更新用户总积分
        userRepository.findById(userId).ifPresent(user -> {
            user.setPoints(user.getPoints() + points);
            userRepository.save(user);
        });

        return pointsRecordRepository.save(record);
    }

    /**
     * 每日签到
     */
    public Map<String, Object> dailySignIn(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 检查今日是否已签到
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        boolean alreadySigned = pointsRecordRepository.existsByUserIdAndTypeAndCreatedAtBetween(
                userId, "DAILY_SIGNIN", todayStart, todayEnd);

        if (alreadySigned) {
            result.put("success", false);
            result.put("message", "今日已签到");
            return result;
        }

        // 执行签到
        PointsRecord record = addPoints(userId, "DAILY_SIGNIN", "每日签到");

        // 检查连续签到天数
        int continuousDays = getContinuousSignInDays(userId);

        result.put("success", true);
        result.put("points", record.getPoints());
        result.put("continuousDays", continuousDays);
        result.put("message", "签到成功，获得" + record.getPoints() + "积分");

        // 连续签到7天奖励
        if (continuousDays == 7) {
            addPoints(userId, "CONTINUOUS_SIGNIN_7", "连续签到7天奖励");
            result.put("message", result.get("message") + "，连续签到7天额外奖励50积分！");
        }

        return result;
    }

    /**
     * 计算连续签到天数
     */
    private int getContinuousSignInDays(Long userId) {
        List<PointsRecord> records = pointsRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);

        int days = 0;
        LocalDate checkDate = LocalDate.now();

        for (PointsRecord record : records) {
            if (!"DAILY_SIGNIN".equals(record.getType())) {
                continue;
            }

            LocalDate recordDate = record.getCreatedAt().toLocalDate();

            if (recordDate.equals(checkDate)) {
                days++;
                checkDate = checkDate.minusDays(1);
            } else if (recordDate.isBefore(checkDate)) {
                break;
            }
        }

        return days;
    }

    /**
     * 获取用户积分统计
     */
    public Map<String, Object> getUserPointsStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 总积分
        Integer totalPoints = pointsRecordRepository.getTotalPointsByUserId(userId);
        stats.put("totalPoints", totalPoints);

        // 今日获得积分
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<PointsRecord> todayRecords = pointsRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, todayStart, todayEnd);
        int todayPoints = todayRecords.stream().mapToInt(PointsRecord::getPoints).sum();
        stats.put("todayPoints", todayPoints);

        // 连续签到天数
        int continuousDays = getContinuousSignInDays(userId);
        stats.put("continuousSignInDays", continuousDays);

        // 今日是否已签到
        boolean todaySigned = pointsRecordRepository.existsByUserIdAndTypeAndCreatedAtBetween(
                userId, "DAILY_SIGNIN", todayStart, todayEnd);
        stats.put("todaySigned", todaySigned);

        // 各类型积分统计
        Map<String, Integer> typeStats = new HashMap<>();
        for (String type : POINTS_RULES.keySet()) {
            long count = pointsRecordRepository.countByUserIdAndType(userId, type);
            typeStats.put(type, (int) count);
        }
        stats.put("typeStats", typeStats);

        return stats;
    }

    /**
     * 获取用户积分记录
     */
    public List<PointsRecord> getUserPointsRecords(Long userId) {
        return pointsRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 扣除积分（用于兑换等）
     */
    public boolean deductPoints(Long userId, Integer points, String description) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getPoints() < points) {
            return false;
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-points);
        record.setType("EXCHANGE");
        record.setDescription(description);
        pointsRecordRepository.save(record);

        user.setPoints(user.getPoints() - points);
        userRepository.save(user);

        return true;
    }
}
