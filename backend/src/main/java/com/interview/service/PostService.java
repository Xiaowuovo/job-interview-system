package com.interview.service;

import com.interview.entity.Post;
import com.interview.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
    }

    @Transactional
    public Post updatePost(Long id, Post post) {
        Post existingPost = getPostById(id);
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setTags(post.getTags());
        existingPost.setCompany(post.getCompany());
        existingPost.setPosition(post.getPosition());
        return postRepository.save(existingPost);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getPostsByType(Post.PostType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByTypeOrderByCreatedAtDesc(type, pageable);
    }

    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Post> getTopAndEssencePosts() {
        return postRepository.findTopAndEssencePosts();
    }

    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.searchByKeyword(keyword, pageable);
    }

    public Page<Post> getPostsByCompany(String company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByCompany(company, pageable);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void incrementLikeCount(Long id) {
        Post post = getPostById(id);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void incrementCommentCount(Long id) {
        Post post = getPostById(id);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void setTopStatus(Long id, boolean isTop) {
        Post post = getPostById(id);
        post.setIsTop(isTop);
        postRepository.save(post);
    }

    @Transactional
    public void setEssenceStatus(Long id, boolean isEssence) {
        Post post = getPostById(id);
        post.setIsEssence(isEssence);
        postRepository.save(post);
    }

    public List<Post> getHotPosts() {
        return postRepository.findTop10ByOrderByViewCountDesc();
    }
}
