package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.KnowledgeCategory;
import com.interview.service.KnowledgeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge-categories")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeCategoryService categoryService;

    @PostMapping
    public Result<KnowledgeCategory> createCategory(@RequestBody KnowledgeCategory category) {
        KnowledgeCategory created = categoryService.createCategory(category);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<KnowledgeCategory> getCategory(@PathVariable Long id) {
        KnowledgeCategory category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<KnowledgeCategory> updateCategory(@PathVariable Long id, @RequestBody KnowledgeCategory category) {
        KnowledgeCategory updated = categoryService.updateCategory(id, category);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/root")
    public Result<List<KnowledgeCategory>> getRootCategories() {
        List<KnowledgeCategory> categories = categoryService.getRootCategories();
        return Result.success(categories);
    }

    @GetMapping("/children/{parentId}")
    public Result<List<KnowledgeCategory>> getChildCategories(@PathVariable Long parentId) {
        List<KnowledgeCategory> categories = categoryService.getChildCategories(parentId);
        return Result.success(categories);
    }

    @GetMapping("/tree")
    public Result<List<KnowledgeCategory>> getCategoryTree() {
        List<KnowledgeCategory> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @GetMapping("/level/{level}")
    public Result<List<KnowledgeCategory>> getCategoriesByLevel(@PathVariable Integer level) {
        List<KnowledgeCategory> categories = categoryService.getCategoriesByLevel(level);
        return Result.success(categories);
    }

    @GetMapping("/all")
    public Result<List<KnowledgeCategory>> getAllCategories() {
        List<KnowledgeCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}
