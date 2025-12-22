package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.KnowledgePoint;
import com.interview.entity.StudyRecord;
import com.interview.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识点控制器
 */
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 获取所有知识点
     */
    @GetMapping
    public Result<List<KnowledgePoint>> getAllKnowledgePoints() {
        return Result.success(knowledgeService.getAllKnowledgePoints());
    }

    /**
     * 根据ID获取知识点
     */
    @GetMapping("/{id}")
    public Result<KnowledgePoint> getKnowledgePointById(@PathVariable Long id) {
        return knowledgeService.getKnowledgePointById(id)
                .map(Result::success)
                .orElse(Result.error("知识点不存在"));
    }

    /**
     * 根据分类获取知识点
     */
    @GetMapping("/category/{category}")
    public Result<List<KnowledgePoint>> getByCategory(@PathVariable String category) {
        return Result.success(knowledgeService.getKnowledgePointsByCategory(category));
    }

    /**
     * 根据难度获取知识点
     */
    @GetMapping("/difficulty/{difficulty}")
    public Result<List<KnowledgePoint>> getByDifficulty(@PathVariable String difficulty) {
        return Result.success(knowledgeService.getKnowledgePointsByDifficulty(difficulty));
    }

    /**
     * 搜索知识点
     */
    @GetMapping("/search")
    public Result<List<KnowledgePoint>> searchKnowledgePoints(@RequestParam String keyword) {
        return Result.success(knowledgeService.searchKnowledgePoints(keyword));
    }

    /**
     * 获取热门知识点
     */
    @GetMapping("/hot")
    public Result<List<KnowledgePoint>> getHotKnowledgePoints() {
        return Result.success(knowledgeService.getHotKnowledgePoints());
    }

    /**
     * 创建知识点（教师权限）
     */
    @PostMapping
    public Result<KnowledgePoint> createKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        KnowledgePoint created = knowledgeService.createKnowledgePoint(knowledgePoint);
        return Result.success(created);
    }

    /**
     * 更新知识点（教师权限）
     */
    @PutMapping("/{id}")
    public Result<KnowledgePoint> updateKnowledgePoint(
            @PathVariable Long id,
            @RequestBody KnowledgePoint knowledgePoint) {
        KnowledgePoint updated = knowledgeService.updateKnowledgePoint(id, knowledgePoint);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("知识点不存在");
    }

    /**
     * 删除知识点（教师权限）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledgePoint(@PathVariable Long id) {
        boolean deleted = knowledgeService.deleteKnowledgePoint(id);
        if (deleted) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }

    /**
     * 增加浏览次数
     */
    @PostMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        knowledgeService.incrementViewCount(id);
        return Result.success(null);
    }

    /**
     * 记录学习进度
     */
    @PostMapping("/study")
    public Result<StudyRecord> recordStudyProgress(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long knowledgePointId = Long.valueOf(params.get("knowledgePointId").toString());
        Integer progress = Integer.valueOf(params.get("progress").toString());
        Integer studyTime = Integer.valueOf(params.get("studyTime").toString());

        StudyRecord record = knowledgeService.recordStudyProgress(
                userId, knowledgePointId, progress, studyTime);
        return Result.success(record);
    }

    /**
     * 获取用户学习记录
     */
    @GetMapping("/study/user/{userId}")
    public Result<List<StudyRecord>> getUserStudyRecords(@PathVariable Long userId) {
        return Result.success(knowledgeService.getUserStudyRecords(userId));
    }

    /**
     * 获取用户对特定知识点的学习记录
     */
    @GetMapping("/study/{userId}/{knowledgePointId}")
    public Result<StudyRecord> getUserKnowledgePointRecord(
            @PathVariable Long userId,
            @PathVariable Long knowledgePointId) {
        return knowledgeService.getUserKnowledgePointRecord(userId, knowledgePointId)
                .map(Result::success)
                .orElse(Result.error("未找到学习记录"));
    }

    /**
     * 获取用户学习统计
     */
    @GetMapping("/statistics/{userId}")
    public Result<Map<String, Object>> getUserStudyStatistics(@PathVariable Long userId) {
        return Result.success(knowledgeService.getUserStudyStatistics(userId));
    }

    /**
     * 推荐知识点
     */
    @GetMapping("/recommend/{userId}")
    public Result<List<KnowledgePoint>> recommendKnowledgePoints(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(knowledgeService.recommendKnowledgePoints(userId, limit));
    }
}
