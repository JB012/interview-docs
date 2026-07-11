package com.interviewdocs.server.repository;

import java.util.List;
import java.util.Set;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import io.micronaut.data.repository.PageableRepository;
import com.interviewdocs.server.model.Question;
import io.micronaut.data.annotation.*;
import java.util.Optional;
import io.micronaut.context.annotation.Parameter;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Join(value = "folders", type = Join.Type.LEFT_FETCH)
    Optional<Question> findById(Long id);
    @Join(value = "videos", type = Join.Type.LEFT_FETCH)
    @Query (value = "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.videos WHERE q.id = :id", 
        countQuery = "SELECT count(q) FROM Question q WHERE q.id = :id")
    Optional<Question> findByQuestionId(@Parameter("id") Long id);
    List<Question> findAllByUserId(String userId);
    Page<Question> findAllByUserId(String userId, Pageable pageable);
    Page<Question> findByUserIdAndIdIn(String userId, Set<Long> ids, Pageable pageable);
}
