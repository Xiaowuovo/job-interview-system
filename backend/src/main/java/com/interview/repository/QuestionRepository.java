package com.interview.repository;

import com.interview.entity.Question;
import com.interview.entity.Question.Difficulty;
import com.interview.entity.Question.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 按分类查询
    List<Question> findByCategory(String category);

    // 按分类和难度查询
    List<Question> findByCategoryAndDifficulty(String category, Difficulty difficulty);

    // 按题型查询
    List<Question> findByType(QuestionType type);

    // 按标签查询
    @Query("SELECT q FROM Question q WHERE q.tags LIKE %:tag%")
    List<Question> findByTag(@Param("tag") String tag);

    // 随机获取题目
    @Query(value = "SELECT * FROM questions WHERE category = ?1 ORDER BY RAND() LIMIT ?2", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(String category, int limit);

    // 随机获取所有题目
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Question> findRandomQuestions(int limit);

    // 按浏览量排序
    List<Question> findTop100ByOrderByViewCountDesc();

    // 按通过率排序
    List<Question> findTop50ByOrderByPassRateDesc();

    // 查询特定分类和难度的题目数量
    Long countByCategoryAndDifficulty(String category, Difficulty difficulty);

    // 查询用户创建的题目
    List<Question> findByCreatedBy(Long userId);

    // 全文搜索（标题和内容）
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%")
    List<Question> searchByKeyword(@Param("keyword") String keyword);
}
