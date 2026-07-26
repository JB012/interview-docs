package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
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
        new Question(USER_ID, "apple?", OffsetDateTime.parse("2026-07-20T08:36:10Z"), 
        OffsetDateTime.parse("2026-07-21T16:09:50Z")),
        new Question(USER_ID, "banana?", OffsetDateTime.parse("2026-06-08T11:09:57Z"), 
        OffsetDateTime.parse("2026-06-09T12:04:23Z")),
        new Question(USER_ID, "cherry?", OffsetDateTime.parse("2026-05-12T15:43:10Z"), 
        OffsetDateTime.parse("2026-05-13T01:34:38Z"))
    );

    void testgetQuestionsSortHelper(int page, int size, String sort, List<String> expected) {
        PagedResponse<Question> questionPage = questionService.getQuestions(page, size, sort, USER_ID);
        
        List<String> result = questionPage.getContent().stream().map(question -> question.getQuestion()).toList();

        assertEquals(expected, result);
        assertEquals(page, questionPage.getPageNumber());
        assertEquals(size, questionPage.getTotalSize());
        assertEquals(size, questionPage.getPageSize());
    }

    @Test
    void testgetQuestionsSort() {
        int page = 0, size = questions.size();

        questionRepository.insertAll(questions);

        testgetQuestionsSortHelper(page, size, "question, asc", List.of("apple?", "banana?", "cherry?"));
        testgetQuestionsSortHelper(page, size, "question, desc", List.of("cherry?", "banana?", "apple?"));        

        testgetQuestionsSortHelper(page, size, "editedAt, asc", List.of("cherry?", "banana?", "apple?"));
        testgetQuestionsSortHelper(page, size, "editedAt, desc", List.of("apple?", "banana?", "cherry?"));

        testgetQuestionsSortHelper(page, size, "viewedAt, asc", List.of("cherry?", "banana?", "apple?"));
        testgetQuestionsSortHelper(page, size, "viewedAt, desc", List.of("apple?", "banana?", "cherry?"));
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
