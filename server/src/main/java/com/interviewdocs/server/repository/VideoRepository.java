package com.interviewdocs.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByUserId(String userId);
    Page<Video> findAllByUserId(String userId, Pageable pageable);
}
