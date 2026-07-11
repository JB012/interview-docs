package com.interviewdocs.server.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import io.micronaut.data.model.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.QuestionService;
import com.interviewdocs.server.services.S3Service;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;
import com.interviewdocs.server.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@RestController
public class QuestionController {
    private final QuestionRepository questionRepository;
    private final VideoRepository videoRepository;
    private final QuestionService questionService;
    private final S3Service s3Service;

    private static final String BUCKET_NAME = "interviewdocs-videos";

    QuestionController(QuestionRepository questionRepository, VideoRepository videoRepository,
        QuestionService questionService, S3Service s3Service) {
        this.questionRepository = questionRepository;
        this.videoRepository = videoRepository;
        this.questionService = questionService;
        this.s3Service = s3Service;
    }

    @GetMapping("/questions/all")
    ResponseEntity<List<Question>> getAllQuestions(Principal auth) {
        return ResponseEntity.ok(questionRepository.findAllByUserId(auth.getName()));
       
    }
    
    @GetMapping("/questions")
    PagedResponse<Question> getQuestions(Principal auth, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewedAt, desc") String sort) {
        return questionService.getQuestions(page, size, sort, auth.getName());
    }
    
    @PostMapping("/questions")
    ResponseEntity<Question> newQuestion(@RequestBody Question newQuestion) {
        return ResponseEntity.ok(questionRepository.save(newQuestion));
    }

    @GetMapping("/questions/{id:[0-9]+}")
    ResponseEntity<Question> one(@PathVariable("id") Long id) {
        Question question = questionRepository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));

        return ResponseEntity.ok(question);
    }

    @GetMapping("/questions/{questionId}/folders")
    ResponseEntity<Set<Folder>> getFolders(@PathVariable("questionId") Long id) {
        Question question = questionRepository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));
    
        return ResponseEntity.ok(question.getFolders());
    }

    @PutMapping("/questions/{id}")
    void replaceQuestion(Principal auth, @RequestBody Question newQuestion, @PathVariable("id") Long id) {
        if (newQuestion.getUserId().equals(auth.getName())) {    
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
    void deleteQuestion(@PathVariable("id") Long id) {
        Question question = questionRepository.findByQuestionId(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));

        
        Set<Video> videos = question.getVideos();

        for (Video v : videos) {
            s3Service.deleteS3Object(BUCKET_NAME, v.getKeyName());
        }
        
        videoRepository.deleteByQuestion(question);
        questionRepository.deleteById(id);
    } 
}
