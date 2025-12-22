package com.interview.repository;

import com.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    
    List<InterviewQuestion> findBySessionIdOrderByQuestionOrderAsc(Long sessionId);
    
    Long countBySessionId(Long sessionId);
    
    List<InterviewQuestion> findBySessionIdAndQuestionTypeOrderByQuestionOrderAsc(
        Long sessionId, InterviewQuestion.QuestionType questionType);
}
