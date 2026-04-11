package com.interviewdocs.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
