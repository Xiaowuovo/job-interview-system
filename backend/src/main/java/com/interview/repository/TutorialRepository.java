package com.interview.repository;

import com.interview.entity.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    List<Tutorial> findByCategory(String category);
    List<Tutorial> findAllByOrderByCreatedAtDesc();
    
    /**
     * 按浏览量倒序查询前10条
     */
    List<Tutorial> findTop10ByOrderByViewCountDesc();

    /**
     * 按标题模糊搜索
     */
    List<Tutorial> findByTitleContainingIgnoreCase(String keyword);

    /**
     * 按标题或内容模糊搜索
     */
    List<Tutorial> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
