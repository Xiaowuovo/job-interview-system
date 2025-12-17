package com.interview.service;

import com.interview.entity.WrongQuestionBook;
import com.interview.repository.WrongQuestionBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 错题本服务
 */
@Service
@Transactional
public class WrongQuestionBookService {

    @Autowired
    private WrongQuestionBookRepository wrongQuestionBookRepository;

    // 复习时间间隔（天）- 基于艾宾浩斯遗忘曲线
    private static final int[] REVIEW_INTERVALS = {1, 2, 4, 7, 15};

    /**
     * 添加错题
     */
    public WrongQuestionBook addWrongQuestion(Long userId, Long questionId) {
        Optional<WrongQuestionBook> existing = wrongQuestionBookRepository
                .findByUserIdAndQuestionId(userId, questionId);

        WrongQuestionBook wrongQuestion;
        if (existing.isPresent()) {
            // 已存在，增加错误次数
            wrongQuestion = existing.get();
            wrongQuestion.setWrongCount(wrongQuestion.getWrongCount() + 1);
            wrongQuestion.setIsMastered(false); // 重新标记为未掌握
        } else {
            // 新错题
            wrongQuestion = new WrongQuestionBook();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setReviewCount(0);
            wrongQuestion.setIsMastered(false);
            wrongQuestion.setCreatedAt(LocalDateTime.now());
        }

        // 设置下次复习时间（1天后）
        wrongQuestion.setLastReviewAt(LocalDateTime.now());
        wrongQuestion.setNextReviewAt(LocalDateTime.now().plusDays(REVIEW_INTERVALS[0]));

        return wrongQuestionBookRepository.save(wrongQuestion);
    }

    /**
     * 获取用户的错题本
     */
    public List<WrongQuestionBook> getUserWrongQuestions(Long userId) {
        return wrongQuestionBookRepository.findByUserId(userId);
    }

    /**
     * 获取用户未掌握的错题
     */
    public List<WrongQuestionBook> getUnmasteredQuestions(Long userId) {
        return wrongQuestionBookRepository.findByUserIdAndIsMastered(userId, false);
    }

    /**
     * 获取需要复习的错题
     */
    public List<WrongQuestionBook> getQuestionsNeedReview(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return wrongQuestionBookRepository.findByUserIdAndNextReviewAtBeforeAndIsMastered(
                userId, now, false);
    }

    /**
     * 记录复习
     */
    public WrongQuestionBook recordReview(Long userId, Long questionId, boolean mastered) {
        Optional<WrongQuestionBook> wqOpt = wrongQuestionBookRepository
                .findByUserIdAndQuestionId(userId, questionId);

        if (wqOpt.isEmpty()) {
            return null;
        }

        WrongQuestionBook wq = wqOpt.get();
        wq.setReviewCount(wq.getReviewCount() + 1);
        wq.setLastReviewAt(LocalDateTime.now());

        if (mastered) {
            wq.setIsMastered(true);
            wq.setNextReviewAt(null); // 已掌握，不再需要复习
        } else {
            // 根据复习次数设置下次复习时间
            int reviewCount = wq.getReviewCount();
            if (reviewCount < REVIEW_INTERVALS.length) {
                int daysToAdd = REVIEW_INTERVALS[reviewCount];
                wq.setNextReviewAt(LocalDateTime.now().plusDays(daysToAdd));
            } else {
                // 超过预设复习次数，设置为30天后
                wq.setNextReviewAt(LocalDateTime.now().plusDays(30));
            }
        }

        return wrongQuestionBookRepository.save(wq);
    }

    /**
     * 添加笔记
     */
    public WrongQuestionBook addNotes(Long userId, Long questionId, String notes) {
        Optional<WrongQuestionBook> wqOpt = wrongQuestionBookRepository
                .findByUserIdAndQuestionId(userId, questionId);

        if (wqOpt.isEmpty()) {
            return null;
        }

        WrongQuestionBook wq = wqOpt.get();
        wq.setNotes(notes);
        return wrongQuestionBookRepository.save(wq);
    }

    /**
     * 从错题本移除
     */
    public boolean removeWrongQuestion(Long userId, Long questionId) {
        Optional<WrongQuestionBook> wqOpt = wrongQuestionBookRepository
                .findByUserIdAndQuestionId(userId, questionId);

        if (wqOpt.isPresent()) {
            wrongQuestionBookRepository.delete(wqOpt.get());
            return true;
        }
        return false;
    }

    /**
     * 检查是否在错题本中
     */
    public boolean isInWrongBook(Long userId, Long questionId) {
        return wrongQuestionBookRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .isPresent();
    }

    /**
     * 获取错题统计
     */
    public java.util.Map<String, Object> getWrongQuestionStatistics(Long userId) {
        List<WrongQuestionBook> wrongQuestions = wrongQuestionBookRepository.findByUserId(userId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 总错题数
        stats.put("totalWrongQuestions", wrongQuestions.size());

        // 已掌握数量
        long masteredCount = wrongQuestions.stream()
                .filter(WrongQuestionBook::getIsMastered)
                .count();
        stats.put("masteredCount", masteredCount);

        // 未掌握数量
        long unmasteredCount = wrongQuestions.size() - masteredCount;
        stats.put("unmasteredCount", unmasteredCount);

        // 需要复习的数量
        LocalDateTime now = LocalDateTime.now();
        long needReviewCount = wrongQuestions.stream()
                .filter(wq -> !wq.getIsMastered() &&
                        wq.getNextReviewAt() != null &&
                        wq.getNextReviewAt().isBefore(now))
                .count();
        stats.put("needReviewCount", needReviewCount);

        // 总复习次数
        int totalReviewCount = wrongQuestions.stream()
                .mapToInt(WrongQuestionBook::getReviewCount)
                .sum();
        stats.put("totalReviewCount", totalReviewCount);

        // 平均错误次数
        double avgWrongCount = wrongQuestions.stream()
                .mapToInt(WrongQuestionBook::getWrongCount)
                .average()
                .orElse(0.0);
        stats.put("averageWrongCount", Math.round(avgWrongCount * 100.0) / 100.0);

        // 掌握率
        double masteryRate = wrongQuestions.isEmpty() ? 0.0 :
                ((double) masteredCount / wrongQuestions.size() * 100);
        stats.put("masteryRate", Math.round(masteryRate * 100.0) / 100.0);

        return stats;
    }

    /**
     * 清空已掌握的错题
     */
    public int clearMasteredQuestions(Long userId) {
        List<WrongQuestionBook> mastered = wrongQuestionBookRepository
                .findByUserIdAndIsMastered(userId, true);

        wrongQuestionBookRepository.deleteAll(mastered);
        return mastered.size();
    }

    /**
     * 批量标记为已掌握
     */
    public int markAsMastered(Long userId, List<Long> questionIds) {
        int count = 0;
        for (Long questionId : questionIds) {
            Optional<WrongQuestionBook> wqOpt = wrongQuestionBookRepository
                    .findByUserIdAndQuestionId(userId, questionId);
            if (wqOpt.isPresent()) {
                WrongQuestionBook wq = wqOpt.get();
                wq.setIsMastered(true);
                wq.setNextReviewAt(null);
                wrongQuestionBookRepository.save(wq);
                count++;
            }
        }
        return count;
    }
}
