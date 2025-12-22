package com.interview.repository;

import com.interview.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByTypeOrderByCreatedAtDesc(Post.PostType type, Pageable pageable);
    
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isTop = true OR p.isEssence = true ORDER BY p.isTop DESC, p.isEssence DESC, p.createdAt DESC")
    List<Post> findTopAndEssencePosts();
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.company = :company ORDER BY p.createdAt DESC")
    Page<Post> findByCompany(@Param("company") String company, Pageable pageable);
    
    List<Post> findTop10ByOrderByViewCountDesc();
}
