package com.interviewdocs.repository;

import java.util.List;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.annotation.*;
import io.micronaut.context.annotation.Parameter;
import com.interviewdocs.model.Folder;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    @Join(value = "questions", type = Join.Type.LEFT_FETCH) 
    Optional<Folder> findById(Long id);
    List<Folder> findAllByUserId(String userId);
    Page<Folder> findAllByUserId(String userId, Pageable pageable);
    @Query(value = "SELECT f FROM Folder f WHERE f.userId = :userId AND EXISTS (SELECT 1 FROM f.questions q WHERE q.id = :questionId)",
        countQuery = "SELECT count(*) FROM Folder f WHERE f.userId = :userId AND EXISTS (SELECT 1 FROM f.questions q WHERE q.id = :questionId)"
    )
    List<Folder> findAllByUserIdAndQuestionId(@Parameter("userId") String userId, @Parameter("questionId") Long id);
}