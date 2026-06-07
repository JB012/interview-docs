package com.interviewdocs.server.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.interviewdocs.server.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByUserId(String userId);
    Page<Question> findAllByUserId(String userId, Pageable pageable);
    @Query("SELECT q FROM Question q WHERE q in :questions AND q.userId = :userId")
    Page<Question> findAllByUserId(@Param("userId") String userId, @Param("questions") Set<Question> questions, Pageable pageable);
}
