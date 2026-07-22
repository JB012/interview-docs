package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.interviewdocs.model.Question;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.services.QuestionService;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "test")
public class QuestionServiceIntegTest {
    @Inject
    QuestionService questionService;

    @Inject
    QuestionRepository questionRepository;

    final static String USER_ID = "1234";

    @Test
    void test() {
        Question question = new Question(1L, USER_ID, "What is your question?");

        questionRepository.insert(question);

        assertNotNull(question.getId());
    }
}
