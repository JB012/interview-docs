package com.interviewdocs.server.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.QuestionService;

@RestController
public class QuestionController {
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    QuestionController(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    @GetMapping("/questions/all")
    ResponseEntity<List<Question>> getAllQuestions(Authentication auth) {
        if (auth.isAuthenticated()) {
            return ResponseEntity.ok(questionRepository.findAllByUserId(auth.getName()));
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
            return ResponseEntity.ok(questionRepository.save(newQuestion));
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/questions/{id:[0-9]+}")
    ResponseEntity<Question> one(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Question question = questionRepository.findById(id)
            .orElseThrow(() -> new QuestionNotFoundException(id));
        
            return ResponseEntity.ok(question);
        }   

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/questions/{id}/folders")
    ResponseEntity<Set<Folder>> getFolders(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Question question = questionRepository.findById(id)
            .orElseThrow(() -> new QuestionNotFoundException(id));
        
            return ResponseEntity.ok(question.getFolders());
        }   

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/questions/{id}")
    void replaceQuestion(Authentication auth, @RequestBody Question newQuestion, @PathVariable("id") Long id) {
        if (auth.isAuthenticated() && newQuestion.getUserId().equals(auth.getName())) {    
            questionRepository.findById(id)
            .map(question -> {
                question.setEverything(newQuestion);
                return questionRepository.save(question);
            })
            .orElseGet(() -> {
                return questionRepository.save(newQuestion);
            });
        }
    }

    @DeleteMapping("/questions/{id}")
    void deleteQuestion(Authentication auth, @PathVariable("id") Long id) {
        if (auth.isAuthenticated()) {
            Question question = questionRepository.findById(id)
            .orElseThrow(() -> new QuestionNotFoundException(id));

            question.removeAllVideos();
            questionRepository.deleteById(id);
        }
    } 
}
