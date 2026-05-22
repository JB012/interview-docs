package com.interviewdocs.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
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

    @GetMapping("/questions")
    PagedModel<Question> getQuestions(@RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "7") int size, @RequestParam(name = "sort", defaultValue = "created_at, desc") String sort) {
        return new PagedModel<>(questionService.getQuestions(page, size, sort));
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
            question.setEverything(newQuestion);
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
