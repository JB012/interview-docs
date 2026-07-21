package com.interviewdocs.server;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
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
import org.mockito.ArgumentCaptor;

import com.interviewdocs.model.Question;
import com.interviewdocs.repository.*;
import com.interviewdocs.services.QuestionService;
import com.interviewdocs.services.S3Service;
import com.interviewdocs.utils.PagedResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.micronaut.data.model.Sort;

@MicronautTest(environments = "test")
public class QuestionServiceUnitTest {
    @Inject
    QuestionRepository questionRepository;
    
    @Inject
    QuestionService questionService;

    @Inject
    @Client("/")
    Rx3HttpClient client; 

    final static String USER_ID = "1234";

    @Test
    void testPagedResponse() {
        List<Question> questions = List.of(
            new Question(3L, USER_ID, "cherry?"),
            new Question(1L, USER_ID, "apple?"),
            new Question(2L, USER_ID, "banana?")
        );

        int page = 0;
        int size = questions.size();
        String field = "question";
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));

        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        when(questionRepository.findAllByUserId(USER_ID, pageable)).thenReturn(questionPage);
        
        PagedResponse<Question> pagedResponseResult = questionService.getQuestions(0, questions.size(), "question,asc", USER_ID);

        assertEquals(questions, pagedResponseResult.getContent());
        assertEquals(size, pagedResponseResult.getTotalSize());
        assertEquals(size, pagedResponseResult.getPageSize());
        assertEquals(page, pagedResponseResult.getPageNumber());
    }

   /*  @Test
    void testAscPageable() {
        List<Question> questions = List.of(
            new Question(3L, USER_ID, "cherry?"),
            new Question(1L, USER_ID, "apple?"),
            new Question(2L, USER_ID, "banana?")
        );
        int page = 0;
        int size = questions.size();
        String field = "question";
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));

        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        when(questionRepository.findAllByUserId(USER_ID, pageable)).thenReturn(questionPage);

        questionService.getQuestions(2, 25, "question,desc", USER_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor =
        ArgumentCaptor.forClass(Pageable.class);

        verify(questionRepository).findAllByUserId(
            eq(USER_ID),
            pageableCaptor.capture()
        );

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(2, pageable.getNumber());
        assertEquals(25, pageable.getSize());

        Sort.Order order = pageable.getSort().getOrderBy().get(0);

        assertEquals("question", order.getProperty());
        assertTrue(order.isAscending());   // or assertFalse() for desc
    } */

    @MockBean(QuestionRepository.class)
    QuestionRepository questionRepository() {
        return mock(QuestionRepository.class);
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
