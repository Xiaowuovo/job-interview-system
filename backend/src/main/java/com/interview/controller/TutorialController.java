package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Tutorial;
import com.interview.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教程控制器 - 优化版
 */
@RestController
@RequestMapping("/api/tutorials")
@CrossOrigin(origins = "*")
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    /**
     * 获取所有教程
     */
    @GetMapping
    public Result<List<Tutorial>> getAllTutorials() {
        return Result.success(tutorialService.getAllTutorials());
    }

    /**
     * 根据ID获取教程（自动增加浏览次数）
     */
    @GetMapping("/{id}")
    public Result<Tutorial> getTutorial(@PathVariable Long id) {
        tutorialService.incrementViewCount(id);
        return tutorialService.getTutorialById(id)
                .map(Result::success)
                .orElse(Result.error("教程不存在"));
    }

    /**
     * 根据分类获取教程
     */
    @GetMapping("/category/{category}")
    public Result<List<Tutorial>> getTutorialsByCategory(@PathVariable String category) {
        return Result.success(tutorialService.getTutorialsByCategory(category));
    }

    /**
     * 搜索教程（按标题或内容）
     */
    @GetMapping("/search")
    public Result<List<Tutorial>> searchTutorials(@RequestParam String keyword) {
        return Result.success(tutorialService.searchTutorials(keyword));
    }

    /**
     * 获取热门教程
     */
    @GetMapping("/hot")
    public Result<List<Tutorial>> getHotTutorials() {
        return Result.success(tutorialService.getHotTutorials());
    }

    /**
     * 创建教程（教师权限）
     */
    @PostMapping
    public Result<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        Tutorial created = tutorialService.createTutorial(tutorial);
        return Result.success(created);
    }

    /**
     * 更新教程（教师权限）
     */
    @PutMapping("/{id}")
    public Result<Tutorial> updateTutorial(@PathVariable Long id, @RequestBody Tutorial tutorial) {
        Tutorial updated = tutorialService.updateTutorial(id, tutorial);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("教程不存在");
    }

    /**
     * 删除教程（教师权限）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTutorial(@PathVariable Long id) {
        boolean deleted = tutorialService.deleteTutorial(id);
        if (deleted) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }
}
