package com.interviewdocs.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.*;


@RestController
public class QuestionController {
    private final QuestionRepository repository;

    QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/questions")
    List<Question> all() {
        return repository.findAll();
    }

    @PostMapping("/questions")
    Question newQuestion(@RequestBody Question newQuestion) {
        return repository.save(newQuestion);
    }

    @GetMapping("/questions/{id}")
    Question one(@PathVariable Long id) {
        return repository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    @PutMapping("/questions/{id}")
    Question replaceQuestion(@RequestBody Question newQuestion, @PathVariable Long id) {
        
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
    void deleteQuestion(@PathVariable Long id) {
        repository.deleteById(id);
    }
    
    
}
