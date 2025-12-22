package com.interview.service;

import com.interview.entity.KnowledgeCategory;
import com.interview.repository.KnowledgeCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeCategoryService {

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    public KnowledgeCategory createCategory(KnowledgeCategory category) {
        if (category.getParentId() != null) {
            KnowledgeCategory parent = categoryRepository.findById(category.getParentId())
                .orElseThrow(() -> new RuntimeException("父分类不存在"));
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setLevel(1);
        }
        return categoryRepository.save(category);
    }

    public KnowledgeCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    @Transactional
    public KnowledgeCategory updateCategory(Long id, KnowledgeCategory category) {
        KnowledgeCategory existing = getCategoryById(id);
        existing.setName(category.getName());
        existing.setIcon(category.getIcon());
        existing.setDescription(category.getDescription());
        existing.setSortOrder(category.getSortOrder());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            throw new RuntimeException("请先删除子分类");
        }
        categoryRepository.deleteById(id);
    }

    public List<KnowledgeCategory> getRootCategories() {
        return categoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
    }

    public List<KnowledgeCategory> getChildCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    public List<KnowledgeCategory> getCategoryTree() {
        List<KnowledgeCategory> rootCategories = getRootCategories();
        for (KnowledgeCategory root : rootCategories) {
            buildTree(root);
        }
        return rootCategories;
    }

    private void buildTree(KnowledgeCategory category) {
        List<KnowledgeCategory> children = getChildCategories(category.getId());
        category.setChildren(children);
        for (KnowledgeCategory child : children) {
            buildTree(child);
        }
    }

    public List<KnowledgeCategory> getCategoriesByLevel(Integer level) {
        return categoryRepository.findByLevelOrderBySortOrderAsc(level);
    }

    public List<KnowledgeCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
