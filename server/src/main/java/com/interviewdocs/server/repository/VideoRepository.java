package com.interviewdocs.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.interviewdocs.server.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT v FROM Video v WHERE v.question.questionId = :questionId AND v.userId = :userId")
    List<Video> findAllByUserIdAndQuestionId(@Param("userId") String userId, @Param("questionId") String questionId);
    @Query("SELECT v FROM Video v WHERE v.question.questionId = :questionId AND v.userId = :userId")
    Page<Video> findAllByUserIdAndQuestionId(@Param("userId") String userId, @Param("questionId") String questionId, Pageable pageable);
}
