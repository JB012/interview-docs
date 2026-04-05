package com.interviewdocs.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewdocs.server.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
}
