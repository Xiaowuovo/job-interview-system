package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Question;
import com.interview.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Result<List<Question>> getAllQuestions() {
        return Result.success(questionService.getAllQuestions());
    }

    @GetMapping("/{id}")
    public Result<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(Result::success)
                .orElse(Result.error("题目不存在"));
    }

    @GetMapping("/category/{category}")
    public Result<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        return Result.success(questionService.getQuestionsByCategory(category));
    }

    @GetMapping("/random")
    public Result<List<Question>> getRandomQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int count) {
        return Result.success(questionService.getRandomQuestions(category, count));
    }

    @GetMapping("/search")
    public Result<List<Question>> searchQuestions(@RequestParam String keyword) {
        return Result.success(questionService.searchQuestions(keyword));
    }

    @GetMapping("/hot")
    public Result<List<Question>> getHotQuestions() {
        return Result.success(questionService.getHotQuestions());
    }

    @PostMapping("/check")
    public Result<Map<String, Object>> checkAnswer(@RequestBody Map<String, Object> request) {
        Long questionId = Long.valueOf(request.get("questionId").toString());
        String userAnswer = request.get("answer").toString();
        Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;

        boolean correct = questionService.checkAnswer(questionId, userAnswer);

        // 更新统计
        double score = correct ? 100.0 : 0.0;
        questionService.updateStatistics(questionId, correct, score);

        return questionService.getQuestionById(questionId)
                .map(question -> {
                    Map<String, Object> resultMap = new java.util.HashMap<>();
                    resultMap.put("correct", correct);
                    resultMap.put("correctAnswer", question.getCorrectAnswer());
                    resultMap.put("explanation", question.getExplanation());
                    resultMap.put("referenceAnswer", question.getReferenceAnswer());
                    resultMap.put("score", score);
                    return Result.success(resultMap);
                })
                .orElse(Result.error("题目不存在"));
    }

    @PostMapping
    public Result<Question> createQuestion(@RequestBody Question question) {
        return Result.success(questionService.createQuestion(question));
    }

    @PutMapping("/{id}")
    public Result<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        return Result.success(questionService.updateQuestion(id, question));
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/view")
    public Result<String> incrementViewCount(@PathVariable Long id) {
        questionService.incrementViewCount(id);
        return Result.success("ok");
    }
}
