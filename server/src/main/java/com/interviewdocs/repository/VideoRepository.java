package com.interviewdocs.server.repository;

import java.util.List;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.model.Question;

import io.micronaut.data.annotation.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT v FROM Video v WHERE v.question.id = :id AND v.userId = :userId")
    List<Video> findAllByUserIdAndQuestionId(@Param("userId") String userId, @Param("id") Long id);
    @Query("SELECT v FROM Video v WHERE v.question.id = :id AND v.userId = :userId")
    Page<Video> findAllByUserIdAndQuestionId(@Param("userId") String userId, @Param("id") Long id, Pageable pageable);
    void deleteByQuestion(Question question);
}
