package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.InterviewAnswer;
import com.interview.entity.InterviewEvaluation;
import com.interview.entity.InterviewQuestion;
import com.interview.service.InterviewQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interview/questions")
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService questionService;

    @PostMapping
    public Result<InterviewQuestion> createQuestion(@RequestBody InterviewQuestion question) {
        InterviewQuestion created = questionService.createQuestion(question);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<InterviewQuestion> getQuestion(@PathVariable Long id) {
        InterviewQuestion question = questionService.getQuestionById(id);
        return Result.success(question);
    }

    @GetMapping("/session/{sessionId}")
    public Result<List<InterviewQuestion>> getSessionQuestions(@PathVariable Long sessionId) {
        List<InterviewQuestion> questions = questionService.getSessionQuestions(sessionId);
        return Result.success(questions);
    }

    @GetMapping("/session/{sessionId}/count")
    public Result<Long> getQuestionCount(@PathVariable Long sessionId) {
        Long count = questionService.getQuestionCount(sessionId);
        return Result.success(count);
    }

    @GetMapping("/session/{sessionId}/type/{type}")
    public Result<List<InterviewQuestion>> getQuestionsByType(
            @PathVariable Long sessionId,
            @PathVariable String type) {
        InterviewQuestion.QuestionType questionType = InterviewQuestion.QuestionType.valueOf(type);
        List<InterviewQuestion> questions = questionService.getQuestionsByType(sessionId, questionType);
        return Result.success(questions);
    }

    @PostMapping("/answer")
    public Result<InterviewAnswer> submitAnswer(@RequestBody InterviewAnswer answer) {
        InterviewAnswer saved = questionService.submitAnswer(answer);
        return Result.success(saved);
    }

    @GetMapping("/question/{questionId}/answer")
    public Result<InterviewAnswer> getAnswer(@PathVariable Long questionId) {
        InterviewAnswer answer = questionService.getAnswerByQuestionId(questionId);
        return Result.success(answer);
    }

    @GetMapping("/session/{sessionId}/evaluations")
    public Result<List<InterviewEvaluation>> getSessionEvaluations(@PathVariable Long sessionId) {
        List<InterviewEvaluation> evaluations = questionService.getSessionEvaluations(sessionId);
        return Result.success(evaluations);
    }

    @GetMapping("/answer/{answerId}/evaluation")
    public Result<InterviewEvaluation> getEvaluation(@PathVariable Long answerId) {
        InterviewEvaluation evaluation = questionService.getEvaluationByAnswerId(answerId);
        return Result.success(evaluation);
    }
}
