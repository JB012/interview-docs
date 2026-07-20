package com.interviewdocs.server;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.rxjava3.http.client.*;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.*;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.interviewdocs.model.Question;
import com.interviewdocs.repository.*;
import com.interviewdocs.services.QuestionService;
import com.interviewdocs.services.S3Service;
import com.interviewdocs.utils.PagedResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

@MicronautTest(environments = "test")
public class QuestionCollaboratorTest {
    @Inject
    QuestionRepository questionRepository;
    
    @Inject
    QuestionService questionService;

    @Inject
    @Client("/")
    Rx3HttpClient client; 

    final static String USER_ID = "1234";

    @Test
    void testAscSort() {
        List<Question> questions = List.of(
            new Question(1L, USER_ID, "apple?"),
            new Question(2L, USER_ID, "banana?"),
            new Question(3L, USER_ID, "cherry?")
        );

        for (Question q : questions) {
            questionRepository.insert(q);
        }


        // Mock repository method in QuestionService (line 32)
        // Don't mock questionService, call the actual method of service
        // compare pagedresponse value
        
      /*   PagedResponse<Question> pagedResponseExpected = new PagedResponse<Question>(questions, questions.size(), 0, questions.size());

        when(questionService.getQuestions(0, questions.size(), "question,asc", USER_ID)).thenReturn(pagedResponseExpected);
        
        PagedResponse<Question> pagedResponseResult = questionService.getQuestions(0, questions.size(), "question,asc", USER_ID);
        
        assertEquals(pagedResponseExpected, pagedResponseResult); */

        List<String> expectedSort = List.of("apple?", "banana?", "cherry?");

        //List<String> resultSort = pagedResponseExpected.getContent().stream().map(question -> question.getQuestion()).toList();

        assertEquals(expectedSort, resultSort);
    }

    @MockBean(QuestionRepository.class)
    QuestionRepository questionRepository() {
        return mock(QuestionRepository.class);
    }

    @MockBean(QuestionService.class)
    QuestionService questionService() {
        return mock(QuestionService.class);
    }

    @MockBean(VideoRepository.class)
    VideoRepository videoRepository() {
        return mock(VideoRepository.class);
    }

    @MockBean(S3Service.class)
    S3Service s3Service() {
        return mock(S3Service.class);
    }
}
