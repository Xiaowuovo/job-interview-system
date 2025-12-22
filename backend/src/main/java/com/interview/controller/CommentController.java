package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Comment;
import com.interview.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment, @RequestHeader("userId") Long userId) {
        comment.setUserId(userId);
        Comment created = commentService.createComment(comment);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<Comment> getComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return Result.success(comment);
    }

    @PutMapping("/{id}")
    public Result<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment, @RequestHeader("userId") Long userId) {
        Comment existingComment = commentService.getCommentById(id);
        if (!existingComment.getUserId().equals(userId)) {
            return Result.error("无权修改他人评论");
        }
        Comment updated = commentService.updateComment(id, comment.getContent());
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        Comment comment = commentService.getCommentById(id);
        if (!comment.getUserId().equals(userId)) {
            return Result.error("无权删除他人评论");
        }
        commentService.deleteComment(id);
        return Result.success();
    }

    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getPostComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    @GetMapping("/post/{postId}/root")
    public Result<List<Comment>> getRootComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getRootComments(postId);
        return Result.success(comments);
    }

    @GetMapping("/parent/{parentId}/replies")
    public Result<List<Comment>> getReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getReplies(parentId);
        return Result.success(replies);
    }

    @GetMapping("/post/{postId}/count")
    public Result<Long> getCommentCount(@PathVariable Long postId) {
        Long count = commentService.getCommentCount(postId);
        return Result.success(count);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Comment>> getUserComments(@PathVariable Long userId) {
        List<Comment> comments = commentService.getUserComments(userId);
        return Result.success(comments);
    }

    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@PathVariable Long id) {
        commentService.incrementLikeCount(id);
        return Result.success();
    }

    @PostMapping("/{id}/best-answer")
    public Result<Void> setBestAnswer(@PathVariable Long id) {
        commentService.setBestAnswer(id);
        return Result.success();
    }
}
