package com.interview.repository;

import com.interview.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {
    
    List<KnowledgeCategory> findByParentIdIsNullOrderBySortOrderAsc();
    
    List<KnowledgeCategory> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    List<KnowledgeCategory> findByLevelOrderBySortOrderAsc(Integer level);
    
    Long countByParentId(Long parentId);
}
