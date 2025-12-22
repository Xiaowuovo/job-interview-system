package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Post;
import com.interview.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Result<Post> createPost(@RequestBody Post post, @RequestHeader("userId") Long userId) {
        post.setUserId(userId);
        Post created = postService.createPost(post);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<Post> getPost(@PathVariable Long id) {
        postService.incrementViewCount(id);
        Post post = postService.getPostById(id);
        return Result.success(post);
    }

    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id, @RequestBody Post post, @RequestHeader("userId") Long userId) {
        Post existingPost = postService.getPostById(id);
        if (!existingPost.getUserId().equals(userId)) {
            return Result.error("无权修改他人帖子");
        }
        Post updated = postService.updatePost(id, post);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        Post post = postService.getPostById(id);
        if (!post.getUserId().equals(userId)) {
            return Result.error("无权删除他人帖子");
        }
        postService.deletePost(id);
        return Result.success();
    }

    @GetMapping
    public Result<Page<Post>> getPosts(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (type != null && !type.isEmpty()) {
            Post.PostType postType = Post.PostType.valueOf(type);
            Page<Post> posts = postService.getPostsByType(postType, page, size);
            return Result.success(posts);
        }
        // 返回所有帖子
        Page<Post> posts = postService.getAllPosts(page, size);
        return Result.success(posts);
    }

    @GetMapping("/user/{userId}")
    public Result<Page<Post>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Post> posts = postService.getPostsByUserId(userId, page, size);
        return Result.success(posts);
    }

    @GetMapping("/top-essence")
    public Result<List<Post>> getTopAndEssencePosts() {
        List<Post> posts = postService.getTopAndEssencePosts();
        return Result.success(posts);
    }

    @GetMapping("/search")
    public Result<Page<Post>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Post> posts = postService.searchPosts(keyword, page, size);
        return Result.success(posts);
    }

    @GetMapping("/company/{company}")
    public Result<Page<Post>> getPostsByCompany(
            @PathVariable String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Post> posts = postService.getPostsByCompany(company, page, size);
        return Result.success(posts);
    }

    @PostMapping("/{id}/like")
    public Result<Void> likePost(@PathVariable Long id) {
        postService.incrementLikeCount(id);
        return Result.success();
    }

    @GetMapping("/hot")
    public Result<List<Post>> getHotPosts() {
        List<Post> posts = postService.getHotPosts();
        return Result.success(posts);
    }

    @PostMapping("/{id}/set-top")
    public Result<Void> setTopStatus(@PathVariable Long id, @RequestParam boolean isTop) {
        postService.setTopStatus(id, isTop);
        return Result.success();
    }

    @PostMapping("/{id}/set-essence")
    public Result<Void> setEssenceStatus(@PathVariable Long id, @RequestParam boolean isEssence) {
        postService.setEssenceStatus(id, isEssence);
        return Result.success();
    }
}
