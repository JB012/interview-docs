package com.interviewdocs.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.QuestionService;

@RestController
public class QuestionController {
    private final QuestionRepository repository;

    @Autowired
    private QuestionService questionService;

    QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/questions/all")
    ResponseEntity<List<Question>> getAllQuestions(Authentication auth) {
        if (auth.isAuthenticated()) {
            List<Question> questions = repository.findAll();

            questions.removeIf(question -> !question.getUserId().equals(auth.getName()));

            return ResponseEntity.ok(questions);
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    
    @GetMapping("/questions")
    PagedModel<Question> getQuestions(Authentication auth, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewed_at, desc") String sort) {
        if (auth.isAuthenticated()) {
            return new PagedModel<>(questionService.getQuestions(page, size, sort, auth.getName()));
        }

        return null;
    }
    
    @PostMapping("/questions")
    ResponseEntity<Question> newQuestion(@RequestBody Question newQuestion, Authentication auth) {
        if (auth.isAuthenticated() && newQuestion.getUserId().equals(auth.getName())) {
            return ResponseEntity.ok(repository.save(newQuestion));
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/questions/{id}")
    ResponseEntity<Question> one(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Question question = repository.findById(id)
            .orElseThrow(() -> new QuestionNotFoundException(id));
        
            return ResponseEntity.ok(question);
        }   

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/questions/{id}")
    void replaceQuestion(Authentication auth, @RequestBody Question newQuestion, @PathVariable("id") Long id) {
        if (auth.isAuthenticated() && newQuestion.getUserId().equals(auth.getName())) {    
            repository.findById(id)
            .map(question -> {
                question.setEverything(newQuestion);
                return repository.save(question);
            })
            .orElseGet(() -> {
                return repository.save(newQuestion);
            });
        }
    }

    @DeleteMapping("/questions/{id}")
    void deleteQuestion(Authentication auth, @PathVariable("id") Long id) {
        if (auth.isAuthenticated()) {
            repository.deleteById(id);
        }
    } 
}
