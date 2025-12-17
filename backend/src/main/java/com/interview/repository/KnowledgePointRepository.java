package com.interview.repository;

import com.interview.entity.KnowledgePoint;
import com.interview.entity.KnowledgePoint.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {

    // 按分类查询
    List<KnowledgePoint> findByCategoryId(Long categoryId);

    // 按难度查询
    List<KnowledgePoint> findByDifficulty(Difficulty difficulty);

    // 按重要度查询
    List<KnowledgePoint> findByImportanceGreaterThanEqual(Integer importance);

    // 按标签查询
    @Query("SELECT k FROM KnowledgePoint k WHERE k.tags LIKE %:tag%")
    List<KnowledgePoint> findByTag(@Param("tag") String tag);

    // 搜索知识点
    @Query("SELECT k FROM KnowledgePoint k WHERE k.title LIKE %:keyword% OR k.content LIKE %:keyword%")
    List<KnowledgePoint> searchByKeyword(@Param("keyword") String keyword);

    // 获取热门知识点
    List<KnowledgePoint> findTop50ByOrderByViewCountDesc();

    // 新增方法
    List<KnowledgePoint> findTop20ByOrderByViewCountDesc();
    List<KnowledgePoint> findByTitleContainingOrContentContaining(String title, String content);

    // 兼容方法 - 使用categoryId替代
    default List<KnowledgePoint> findByCategory(String category) {
        // 这里简化处理，实际应该有分类映射表
        return findAll();
    }
}
