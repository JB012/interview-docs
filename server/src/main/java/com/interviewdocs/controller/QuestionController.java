package com.interviewdocs.controller;

import java.util.List;
import java.util.Set;

import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import com.interviewdocs.model.*;
import com.interviewdocs.services.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;
import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/questions")
public class QuestionController {
    private final QuestionService questionService;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Get("/all")
    HttpResponse<List<Question>> all(Principal auth) {
        return HttpResponse.ok(questionService.getAllQuestions(auth));
    }
    
    @Get
    PagedResponse<Question> getQuestions(Principal auth, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size,  @QueryValue(value = "field", defaultValue = "viewedAt") String field, @QueryValue(value="direction", defaultValue = "desc") String direction) {
        String sort = field + "," + direction;
        return questionService.getQuestions(page, size, sort, auth.getName());
    }
    
    @Post
    HttpResponse<Question> newQuestion(@Body Question newQuestion) {
        return HttpResponse.ok(questionService.saveQuestion(newQuestion));
    }

    @Get("/{id:[0-9]+}")
    HttpResponse<Question> one(@PathVariable("id") Long id) {
        return HttpResponse.ok(questionService.getQuestion(id));
    }

    @Get("/{questionId}/folders")
    HttpResponse<Set<Folder>> getFolders(@PathVariable("questionId") Long id) {
        return HttpResponse.ok(questionService.getFoldersContainingQuestion(id));
    }

    @Put("/{id}")
    void replaceQuestion(@Body Question newQuestion, @PathVariable("id") Long id) {
        questionService.putQuestion(id, newQuestion);
    }

    @Delete("/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        deleteQuestion(id);
    } 
}
