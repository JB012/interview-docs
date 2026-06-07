package com.interviewdocs.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserId(String userId);
    Page<Folder> findAllByUserId(String userId, Pageable pageable);
}