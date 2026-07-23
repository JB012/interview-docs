package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.interviewdocs.model.Question;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.services.QuestionService;
import com.interviewdocs.utils.PagedResponse;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "test")
public class QuestionServiceIntegTest {
    @Inject
    QuestionService questionService;

    @Inject
    QuestionRepository questionRepository;

    final static String USER_ID = "1234";

    final List<Question> questions = List.of(
        new Question(USER_ID, "apple?"),
        new Question(USER_ID, "banana?"),
        new Question(USER_ID, "cherry?")
    );

    @Test
    void testgetQuestionsAscSort() {
        String sort = "question, asc";
        int page = 0, size = questions.size();

        questionRepository.insertAll(questions);

        PagedResponse<Question> questionPage = questionService.getQuestions(page, size, sort, USER_ID);
        
        List<String> expected = List.of("apple?", "banana?", "cherry?");
        List<String> result = questionPage.getContent().stream().map(question -> question.getQuestion()).toList();

        assertEquals(expected, result);
        assertEquals(page, questionPage.getPageNumber());
        assertEquals(size, questionPage.getTotalSize());
        assertEquals(size, questionPage.getPageSize());
    }

    @Test
    void testgetQuestionsDescSort() {
        String sort = "question, desc";
        int page = 0, size = questions.size();
        
        questionRepository.insertAll(questions);

        PagedResponse<Question> questionPage = questionService.getQuestions(0, questions.size(), sort, USER_ID);
        
        List<String> expected = List.of("cherry?", "banana?", "apple?");
        List<String> result = questionPage.getContent().stream().map(question -> question.getQuestion()).toList();

        assertEquals(expected, result);
        assertEquals(page, questionPage.getPageNumber());
        assertEquals(size, questionPage.getTotalSize());
        assertEquals(size, questionPage.getPageSize());
    }

    @Test
    void testgetQuestionsSizeLimit() {
        String sort = "question, asc";
        int pageSize = 1;
        
        questionRepository.insertAll(questions);

        for (int i = 0; i < questions.size(); i++) {
            PagedResponse<Question> questionPage = questionService.getQuestions(i, pageSize, sort, USER_ID);

            assertEquals(List.of(questions.get(i)), questionPage.getContent());
            assertEquals(pageSize, questionPage.getPageSize());
            assertEquals(questions.size(), questionPage.getTotalSize());
            assertEquals(i, questionPage.getPageNumber());
        }
    }
}
