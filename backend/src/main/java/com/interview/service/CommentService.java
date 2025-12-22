package com.interview.service;

import com.interview.entity.Comment;
import com.interview.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Transactional
    public Comment createComment(Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        postService.incrementCommentCount(comment.getPostId());
        return savedComment;
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("评论不存在"));
    }

    @Transactional
    public Comment updateComment(Long id, String content) {
        Comment comment = getCommentById(id);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public List<Comment> getRootComments(Long postId) {
        return commentRepository.findByPostIdAndParentIdIsNullOrderByCreatedAtAsc(postId);
    }

    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
    }

    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public List<Comment> getUserComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void incrementLikeCount(Long id) {
        Comment comment = getCommentById(id);
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    @Transactional
    public void setBestAnswer(Long id) {
        Comment comment = getCommentById(id);
        comment.setIsBestAnswer(true);
        commentRepository.save(comment);
    }
}
