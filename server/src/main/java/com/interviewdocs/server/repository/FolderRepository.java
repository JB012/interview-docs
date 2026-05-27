package com.interviewdocs.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    
}