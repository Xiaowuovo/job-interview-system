package com.interview.service;

import com.interview.entity.Question;
import com.interview.entity.Question.Difficulty;
import com.interview.entity.Question.QuestionType;
import com.interview.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 获取所有题目
     */
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * 根据ID获取题目
     */
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    /**
     * 根据分类获取题目
     */
    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

    /**
     * 根据分类和难度获取题目
     */
    public List<Question> getQuestionsByCategoryAndDifficulty(String category, Difficulty difficulty) {
        return questionRepository.findByCategoryAndDifficulty(category, difficulty);
    }

    /**
     * 随机获取题目
     */
    public List<Question> getRandomQuestions(String category, int limit) {
        if (category != null && !category.isEmpty()) {
            return questionRepository.findRandomQuestionsByCategory(category, limit);
        }
        return questionRepository.findRandomQuestions(limit);
    }

    /**
     * 搜索题目
     */
    public List<Question> searchQuestions(String keyword) {
        return questionRepository.searchByKeyword(keyword);
    }

    /**
     * 获取热门题目
     */
    public List<Question> getHotQuestions() {
        return questionRepository.findTop100ByOrderByViewCountDesc();
    }

    /**
     * 创建题目
     */
    @Transactional
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    /**
     * 更新题目
     */
    @Transactional
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setTitle(questionDetails.getTitle());
        question.setContent(questionDetails.getContent());
        question.setType(questionDetails.getType());
        question.setDifficulty(questionDetails.getDifficulty());
        question.setCategory(questionDetails.getCategory());
        question.setSubCategory(questionDetails.getSubCategory());
        question.setTags(questionDetails.getTags());
        question.setOptionA(questionDetails.getOptionA());
        question.setOptionB(questionDetails.getOptionB());
        question.setOptionC(questionDetails.getOptionC());
        question.setOptionD(questionDetails.getOptionD());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setExplanation(questionDetails.getExplanation());
        question.setReferenceAnswer(questionDetails.getReferenceAnswer());
        question.setKeyPoints(questionDetails.getKeyPoints());

        return questionRepository.save(question);
    }

    /**
     * 删除题目
     */
    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    /**
     * 增加浏览次数
     */
    @Transactional
    public void incrementViewCount(Long id) {
        questionRepository.findById(id).ifPresent(question -> {
            question.setViewCount(question.getViewCount() + 1);
            questionRepository.save(question);
        });
    }

    /**
     * 更新统计数据
     */
    @Transactional
    public void updateStatistics(Long id, boolean isCorrect, double score) {
        questionRepository.findById(id).ifPresent(question -> {
            question.setAttemptCount(question.getAttemptCount() + 1);
            if (isCorrect) {
                question.setPassCount(question.getPassCount() + 1);
            }
            // 更新通过率
            question.setPassRate((double) question.getPassCount() / question.getAttemptCount() * 100);

            // 更新平均分
            double currentTotal = question.getAvgScore() * (question.getAttemptCount() - 1);
            question.setAvgScore((currentTotal + score) / question.getAttemptCount());

            questionRepository.save(question);
        });
    }

    /**
     * 检查答案
     */
    public boolean checkAnswer(Long questionId, String userAnswer) {
        return questionRepository.findById(questionId)
                .map(question -> question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim()))
                .orElse(false);
    }
}
