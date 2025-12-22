package com.interview.repository;

import com.interview.entity.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Long> {
    
    List<InterviewEvaluation> findBySessionIdOrderByIdAsc(Long sessionId);
    
    Optional<InterviewEvaluation> findByAnswerId(Long answerId);
    
    Long countBySessionId(Long sessionId);
}
