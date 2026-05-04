package com.interviewdocs.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.auth0.spring.boot.Auth0AuthenticationToken;
import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.QuestionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class QuestionController {
    private final QuestionRepository repository;

    @Autowired
    private QuestionService questionService;

    QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/questions")
    PagedModel<Question> getQuestions(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name="size", defaultValue = "7") int size) {
        return new PagedModel<>(questionService.getQuestions(page, size));
        
    }
    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @PostMapping("/questions")
    Question newQuestion(@RequestBody Question newQuestion) {
        return repository.save(newQuestion);
    }

    @GetMapping("/questions/{id}")
    Question one(@PathVariable("id") Long id) {
        return repository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    @PutMapping("/questions/{id}")
    Question replaceQuestion(@RequestBody Question newQuestion, @PathVariable("id") Long id) {
        
        return repository.findById(id)
        .map(question -> {
            question.setQuestion(newQuestion.getQuestion());
            return repository.save(question);
        })
        .orElseGet(() -> {
            return repository.save(newQuestion);
        });
    }

    @DeleteMapping("/questions/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
    
    
}
