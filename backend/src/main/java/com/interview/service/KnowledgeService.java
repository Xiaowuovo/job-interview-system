package com.interview.service;

import com.interview.entity.KnowledgePoint;
import com.interview.entity.StudyRecord;
import com.interview.repository.KnowledgePointRepository;
import com.interview.repository.StudyRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 知识点服务
 */
@Service
@Transactional
public class KnowledgeService {

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    /**
     * 获取所有知识点
     */
    public List<KnowledgePoint> getAllKnowledgePoints() {
        return knowledgePointRepository.findAll();
    }

    /**
     * 根据ID获取知识点
     */
    public Optional<KnowledgePoint> getKnowledgePointById(Long id) {
        return knowledgePointRepository.findById(id);
    }

    /**
     * 根据分类获取知识点
     */
    public List<KnowledgePoint> getKnowledgePointsByCategory(String category) {
        return knowledgePointRepository.findByCategory(category);
    }

    /**
     * 根据难度获取知识点
     */
    public List<KnowledgePoint> getKnowledgePointsByDifficulty(String difficulty) {
        return knowledgePointRepository.findByDifficulty(difficulty);
    }

    /**
     * 搜索知识点
     */
    public List<KnowledgePoint> searchKnowledgePoints(String keyword) {
        return knowledgePointRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }

    /**
     * 获取热门知识点
     */
    public List<KnowledgePoint> getHotKnowledgePoints() {
        return knowledgePointRepository.findTop20ByOrderByViewCountDesc();
    }

    /**
     * 创建知识点
     */
    public KnowledgePoint createKnowledgePoint(KnowledgePoint knowledgePoint) {
        return knowledgePointRepository.save(knowledgePoint);
    }

    /**
     * 更新知识点
     */
    public KnowledgePoint updateKnowledgePoint(Long id, KnowledgePoint knowledgePointDetails) {
        Optional<KnowledgePoint> kpOpt = knowledgePointRepository.findById(id);
        if (kpOpt.isPresent()) {
            KnowledgePoint kp = kpOpt.get();
            kp.setTitle(knowledgePointDetails.getTitle());
            kp.setContent(knowledgePointDetails.getContent());
            kp.setCategory(knowledgePointDetails.getCategory());
            kp.setDifficulty(knowledgePointDetails.getDifficulty());
            kp.setImportance(knowledgePointDetails.getImportance());
            kp.setTags(knowledgePointDetails.getTags());
            return knowledgePointRepository.save(kp);
        }
        return null;
    }

    /**
     * 删除知识点
     */
    public boolean deleteKnowledgePoint(Long id) {
        if (knowledgePointRepository.existsById(id)) {
            knowledgePointRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount(Long id) {
        Optional<KnowledgePoint> kpOpt = knowledgePointRepository.findById(id);
        if (kpOpt.isPresent()) {
            KnowledgePoint kp = kpOpt.get();
            kp.setViewCount(kp.getViewCount() + 1);
            knowledgePointRepository.save(kp);
        }
    }

    /**
     * 记录学习进度
     */
    public StudyRecord recordStudyProgress(Long userId, Long knowledgePointId, Integer progress, Integer studyTime) {
        Optional<StudyRecord> existingRecord = studyRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);

        StudyRecord record;
        if (existingRecord.isPresent()) {
            // 更新现有记录
            record = existingRecord.get();
            record.setProgress(progress);
            record.setStudyTime(record.getStudyTime() + studyTime);
            record.setLastStudyAt(LocalDateTime.now());

            // 更新状态
            if (progress >= 100) {
                record.setStatus("COMPLETED");
                record.setMasteryLevel(100);
            } else if (progress > 0) {
                record.setStatus("IN_PROGRESS");
                record.setMasteryLevel(progress);
            }
        } else {
            // 创建新记录
            record = new StudyRecord();
            record.setUserId(userId);
            record.setKnowledgePointId(knowledgePointId);
            record.setProgress(progress);
            record.setStudyTime(studyTime);
            record.setLastStudyAt(LocalDateTime.now());
            record.setStatus(progress >= 100 ? "COMPLETED" : "IN_PROGRESS");
            record.setMasteryLevel(progress);
        }

        return studyRecordRepository.save(record);
    }

    /**
     * 获取用户的学习记录
     */
    public List<StudyRecord> getUserStudyRecords(Long userId) {
        return studyRecordRepository.findByUserId(userId);
    }

    /**
     * 获取用户对特定知识点的学习记录
     */
    public Optional<StudyRecord> getUserKnowledgePointRecord(Long userId, Long knowledgePointId) {
        return studyRecordRepository.findByUserIdAndKnowledgePointId(userId, knowledgePointId);
    }

    /**
     * 获取用户学习进度统计
     */
    public Map<String, Object> getUserStudyStatistics(Long userId) {
        List<StudyRecord> records = studyRecordRepository.findByUserId(userId);

        Map<String, Object> statistics = new HashMap<>();

        // 总学习知识点数
        statistics.put("totalKnowledgePoints", records.size());

        // 已完成数量
        long completedCount = records.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();
        statistics.put("completedCount", completedCount);

        // 学习中数量
        long inProgressCount = records.stream()
                .filter(r -> "IN_PROGRESS".equals(r.getStatus()))
                .count();
        statistics.put("inProgressCount", inProgressCount);

        // 总学习时长(分钟)
        int totalStudyTime = records.stream()
                .mapToInt(StudyRecord::getStudyTime)
                .sum();
        statistics.put("totalStudyTime", totalStudyTime);

        // 平均掌握度
        double avgMastery = records.stream()
                .mapToInt(StudyRecord::getMasteryLevel)
                .average()
                .orElse(0.0);
        statistics.put("averageMasteryLevel", Math.round(avgMastery * 100.0) / 100.0);

        // 完成率
        double completionRate = records.isEmpty() ? 0.0 :
                ((double) completedCount / records.size() * 100);
        statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        return statistics;
    }

    /**
     * 推荐知识点(基于难度和学习记录)
     */
    public List<KnowledgePoint> recommendKnowledgePoints(Long userId, int limit) {
        // 获取用户已学习的知识点
        List<StudyRecord> userRecords = studyRecordRepository.findByUserId(userId);

        // 简单推荐策略: 推荐未学习的、重要度高的知识点
        List<Long> studiedIds = userRecords.stream()
                .map(StudyRecord::getKnowledgePointId)
                .toList();

        // 获取所有知识点，过滤已学习的，按重要度排序
        return knowledgePointRepository.findAll().stream()
                .filter(kp -> !studiedIds.contains(kp.getId()))
                .sorted((a, b) -> Integer.compare(b.getImportance(), a.getImportance()))
                .limit(limit)
                .toList();
    }
}
