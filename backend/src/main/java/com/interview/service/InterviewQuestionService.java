package com.interview.service;

import com.interview.entity.InterviewAnswer;
import com.interview.entity.InterviewQuestion;
import com.interview.entity.InterviewEvaluation;
import com.interview.repository.InterviewAnswerRepository;
import com.interview.repository.InterviewEvaluationRepository;
import com.interview.repository.InterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewQuestionService {

    @Autowired
    private InterviewQuestionRepository questionRepository;

    @Autowired
    private InterviewAnswerRepository answerRepository;

    @Autowired
    private InterviewEvaluationRepository evaluationRepository;

    public InterviewQuestion createQuestion(InterviewQuestion question) {
        return questionRepository.save(question);
    }

    public InterviewQuestion getQuestionById(Long id) {
        return questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("面试问题不存在"));
    }

    public List<InterviewQuestion> getSessionQuestions(Long sessionId) {
        return questionRepository.findBySessionIdOrderByQuestionOrderAsc(sessionId);
    }

    public Long getQuestionCount(Long sessionId) {
        return questionRepository.countBySessionId(sessionId);
    }

    public List<InterviewQuestion> getQuestionsByType(Long sessionId, InterviewQuestion.QuestionType type) {
        return questionRepository.findBySessionIdAndQuestionTypeOrderByQuestionOrderAsc(sessionId, type);
    }

    @Transactional
    public InterviewAnswer submitAnswer(InterviewAnswer answer) {
        InterviewAnswer saved = answerRepository.save(answer);
        
        // 自动评估答案
        InterviewQuestion question = getQuestionById(answer.getQuestionId());
        InterviewEvaluation evaluation = evaluateAnswer(question, answer);
        evaluationRepository.save(evaluation);
        
        return saved;
    }

    public InterviewAnswer getAnswerByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId).orElse(null);
    }

    public List<InterviewEvaluation> getSessionEvaluations(Long sessionId) {
        return evaluationRepository.findBySessionIdOrderByIdAsc(sessionId);
    }

    public InterviewEvaluation getEvaluationByAnswerId(Long answerId) {
        return evaluationRepository.findByAnswerId(answerId).orElse(null);
    }

    private InterviewEvaluation evaluateAnswer(InterviewQuestion question, InterviewAnswer answer) {
        InterviewEvaluation evaluation = new InterviewEvaluation();
        evaluation.setSessionId(question.getSessionId());
        evaluation.setQuestionId(question.getId());
        evaluation.setAnswerId(answer.getId());

        // 简化的评分逻辑（实际应该调用AI服务）
        String userAnswer = answer.getUserAnswer().toLowerCase();
        String standardAnswer = question.getStandardAnswer() != null ? 
            question.getStandardAnswer().toLowerCase() : "";

        // 完整性评分（0-30分）
        double completenessScore = calculateCompletenessScore(userAnswer, question.getExpectedKeyPoints());
        evaluation.setCompletenessScore(completenessScore);

        // 准确性评分（0-40分）
        double accuracyScore = calculateAccuracyScore(userAnswer, standardAnswer);
        evaluation.setAccuracyScore(accuracyScore);

        // 逻辑性评分（0-20分）
        double logicScore = calculateLogicScore(userAnswer);
        evaluation.setLogicScore(logicScore);

        // 表达能力评分（0-10分）
        double expressionScore = calculateExpressionScore(userAnswer);
        evaluation.setExpressionScore(expressionScore);

        // 总分
        double totalScore = completenessScore + accuracyScore + logicScore + expressionScore;
        evaluation.setTotalScore(totalScore);

        // 生成反馈
        generateFeedback(evaluation, totalScore);

        return evaluation;
    }

    private double calculateCompletenessScore(String answer, String keyPoints) {
        if (keyPoints == null || keyPoints.isEmpty()) {
            return 25.0;
        }

        String[] points = keyPoints.split(",");
        int coveredCount = 0;
        for (String point : points) {
            if (answer.contains(point.trim().toLowerCase())) {
                coveredCount++;
            }
        }

        return (double) coveredCount / points.length * 30.0;
    }

    private double calculateAccuracyScore(String userAnswer, String standardAnswer) {
        if (standardAnswer.isEmpty()) {
            return 30.0;
        }

        int matchCount = 0;
        String[] standardWords = standardAnswer.split("\\s+");
        
        for (String word : standardWords) {
            if (word.length() > 3 && userAnswer.contains(word)) {
                matchCount++;
            }
        }

        return Math.min((double) matchCount / standardWords.length * 40.0, 40.0);
    }

    private double calculateLogicScore(String answer) {
        if (answer.length() < 50) return 10.0;
        if (answer.length() < 100) return 15.0;
        return 20.0;
    }

    private double calculateExpressionScore(String answer) {
        if (answer.length() < 30) return 5.0;
        if (answer.length() < 80) return 7.0;
        return 10.0;
    }

    private void generateFeedback(InterviewEvaluation evaluation, double totalScore) {
        if (totalScore >= 80) {
            evaluation.setStrengths("回答全面、准确，表达清晰，逻辑性强");
            evaluation.setSuggestions("继续保持，可以在深度上进一步挖掘");
        } else if (totalScore >= 60) {
            evaluation.setStrengths("基本掌握了核心要点");
            evaluation.setWeaknesses("部分细节不够完善");
            evaluation.setSuggestions("建议加强对关键知识点的理解，提高表达的准确性");
        } else {
            evaluation.setWeaknesses("回答不够完整，缺少关键信息");
            evaluation.setSuggestions("需要系统学习相关知识，多做练习");
        }
    }
}
