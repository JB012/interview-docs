package com.interviewdocs.server.repository;

import java.util.List;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.model.Question;

import io.micronaut.data.annotation.*;
import io.micronaut.context.annotation.Parameter;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query(value = "SELECT v FROM Video v WHERE v.question.id = :id AND v.userId = :userId",
        countQuery = "SELECT count(*) FROM Video v WHERE v.question.id = :id AND v.userId = :userId")
    List<Video> findAllByUserIdAndQuestionId(@Parameter("userId") String userId, @Parameter("id") Long id);
    @Query(value = "SELECT v FROM Video v WHERE v.question.id = :id AND v.userId = :userId",
        countQuery = "SELECT count(*) FROM Video v WHERE v.question.id = :id AND v.userId = :userId"
    )
    Page<Video> findAllByUserIdAndQuestionId(@Parameter("userId") String userId, @Parameter("id") Long id, Pageable pageable);
    void deleteByQuestion(Question question);
    Page<Video> findAllByUserId(String userId, Pageable pageable);
}
