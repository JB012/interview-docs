package com.interviewdocs.server.controller;

import java.util.List;
import java.util.Set;

import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.*;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;
import com.interviewdocs.server.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/questions")
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

    @Get("/all")
    HttpResponse<List<Question>> getAllQuestions(Principal auth) {
        return HttpResponse.ok(questionRepository.findAllByUserId(auth.getName()));
       
    }
    
    @Get
    PagedResponse<Question> getQuestions(Principal auth, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size, @QueryValue(value = "sort", defaultValue = "viewedAt, desc") String sort) {
        return questionService.getQuestions(page, size, sort, auth.getName());
    }
    
    @Post
    HttpResponse<Question> newQuestion(@Body Question newQuestion) {
        return HttpResponse.ok(questionRepository.save(newQuestion));
    }

    @Get("/{id:[0-9]+}")
    HttpResponse<Question> one(@PathVariable("id") Long id) {
        Question question = questionRepository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));

        return HttpResponse.ok(question);
    }

    @Get("/{questionId}/folders")
    HttpResponse<Set<Folder>> getFolders(@PathVariable("questionId") Long id) {
        Question question = questionRepository.findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));
    
        return HttpResponse.ok(question.getFolders());
    }

    @Put("/{id}")
    void replaceQuestion(Principal auth, @Body Question newQuestion, @PathVariable("id") Long id) {
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

    @Delete("/{id}")
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
