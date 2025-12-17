package com.interview.service;

import com.interview.entity.DailyQuestion;
import com.interview.entity.Question;
import com.interview.repository.DailyQuestionRepository;
import com.interview.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * 每日一题服务
 */
@Service
@Transactional
public class DailyQuestionService {

    @Autowired
    private DailyQuestionRepository dailyQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 获取今日一题
     */
    public Map<String, Object> getTodayQuestion() {
        LocalDate today = LocalDate.now();
        Optional<DailyQuestion> dailyOpt = dailyQuestionRepository.findByDate(today);

        DailyQuestion daily;
        if (dailyOpt.isEmpty()) {
            // 今天还没有设置每日一题，自动生成
            daily = generateDailyQuestion(today);
        } else {
            daily = dailyOpt.get();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dailyQuestion", daily);

        // 获取题目详情
        questionRepository.findById(daily.getQuestionId()).ifPresent(question -> {
            result.put("question", question);
        });

        return result;
    }

    /**
     * 自动生成今日一题
     */
    public DailyQuestion generateDailyQuestion(LocalDate date) {
        // 随机选择一道题目
        List<Question> allQuestions = questionRepository.findAll();
        if (allQuestions.isEmpty()) {
            return null;
        }

        Random random = new Random();
        Question selectedQuestion = allQuestions.get(random.nextInt(allQuestions.size()));

        DailyQuestion daily = new DailyQuestion();
        daily.setDate(date);
        daily.setQuestionId(selectedQuestion.getId());
        daily.setCategory(selectedQuestion.getCategory());
        daily.setDifficulty(selectedQuestion.getDifficulty().toString());

        return dailyQuestionRepository.save(daily);
    }

    /**
     * 手动设置每日一题
     */
    public DailyQuestion setDailyQuestion(LocalDate date, Long questionId) {
        Optional<DailyQuestion> existing = dailyQuestionRepository.findByDate(date);

        DailyQuestion daily;
        if (existing.isPresent()) {
            daily = existing.get();
            daily.setQuestionId(questionId);
        } else {
            daily = new DailyQuestion();
            daily.setDate(date);
            daily.setQuestionId(questionId);
        }

        // 获取题目信息
        questionRepository.findById(questionId).ifPresent(question -> {
            daily.setCategory(question.getCategory());
            daily.setDifficulty(question.getDifficulty().toString());
        });

        return dailyQuestionRepository.save(daily);
    }

    /**
     * 记录完成
     */
    public void recordComplete(LocalDate date) {
        Optional<DailyQuestion> dailyOpt = dailyQuestionRepository.findByDate(date);
        dailyOpt.ifPresent(daily -> {
            daily.setCompleteCount(daily.getCompleteCount() + 1);
            dailyQuestionRepository.save(daily);
        });
    }

    /**
     * 获取最近7天的每日一题
     */
    public List<DailyQuestion> getRecentDailyQuestions() {
        return dailyQuestionRepository.findTop7ByOrderByDateDesc();
    }
}
