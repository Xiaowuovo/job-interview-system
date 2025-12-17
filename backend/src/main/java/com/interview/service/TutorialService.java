package com.interview.service;

import com.interview.entity.Tutorial;
import com.interview.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 教程服务
 */
@Service
@Transactional
public class TutorialService {

    @Autowired
    private TutorialRepository tutorialRepository;

    /**
     * 获取所有教程
     */
    public List<Tutorial> getAllTutorials() {
        return tutorialRepository.findAll();
    }

    /**
     * 根据ID获取教程
     */
    public Optional<Tutorial> getTutorialById(Long id) {
        return tutorialRepository.findById(id);
    }

    /**
     * 根据分类获取教程
     */
    public List<Tutorial> getTutorialsByCategory(String category) {
        return tutorialRepository.findByCategory(category);
    }

    /**
     * 创建教程
     */
    public Tutorial createTutorial(Tutorial tutorial) {
        return tutorialRepository.save(tutorial);
    }

    /**
     * 更新教程
     */
    public Tutorial updateTutorial(Long id, Tutorial tutorialDetails) {
        Optional<Tutorial> tutorialOpt = tutorialRepository.findById(id);
        if (tutorialOpt.isPresent()) {
            Tutorial tutorial = tutorialOpt.get();
            tutorial.setTitle(tutorialDetails.getTitle());
            tutorial.setContent(tutorialDetails.getContent());
            tutorial.setCategory(tutorialDetails.getCategory());
            tutorial.setTags(tutorialDetails.getTags());
            tutorial.setDifficulty(tutorialDetails.getDifficulty());
            return tutorialRepository.save(tutorial);
        }
        return null;
    }

    /**
     * 删除教程
     */
    public boolean deleteTutorial(Long id) {
        if (tutorialRepository.existsById(id)) {
            tutorialRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount(Long id) {
        Optional<Tutorial> tutorialOpt = tutorialRepository.findById(id);
        if (tutorialOpt.isPresent()) {
            Tutorial tutorial = tutorialOpt.get();
            tutorial.setViewCount(tutorial.getViewCount() + 1);
            tutorialRepository.save(tutorial);
        }
    }

    /**
     * 获取热门教程(按浏览量排序)
     */
    public List<Tutorial> getHotTutorials() {
        return tutorialRepository.findTop10ByOrderByViewCountDesc();
    }

    /**
     * 搜索教程（按标题或内容模糊搜索）
     */
    public List<Tutorial> searchTutorials(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return tutorialRepository.findAll();
        }
        return tutorialRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword.trim(), keyword.trim());
    }
}