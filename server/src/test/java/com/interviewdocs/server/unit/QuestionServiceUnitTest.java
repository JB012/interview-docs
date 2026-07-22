package com.interviewdocs.server.unit;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.mockito.ArgumentCaptor;

import com.interviewdocs.model.Question;
import com.interviewdocs.repository.*;
import com.interviewdocs.services.QuestionService;
import com.interviewdocs.utils.PagedResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.micronaut.data.model.Sort;

@MicronautTest(environments = "test")
public class QuestionServiceUnitTest {
    @Inject
    QuestionRepository questionRepository;
    
    @Inject
    QuestionService questionService;

    final static String USER_ID = "1234";

    final List<Question> questions = List.of(
        new Question(3L, USER_ID, "cherry?"),
        new Question(1L, USER_ID, "apple?"),
        new Question(2L, USER_ID, "banana?")
    );

    final int page = 0;
    final int size = questions.size();
    final String field = "question";

    @Test
    void testgetQuestionsPagedResponse() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        when(questionRepository.findAllByUserId(USER_ID, pageable)).thenReturn(questionPage);
        
        PagedResponse<Question> pagedResponseResult = questionService.getQuestions(0, questions.size(), "question,asc", USER_ID);

        assertEquals(questions, pagedResponseResult.getContent());
        assertEquals(size, pagedResponseResult.getTotalSize());
        assertEquals(size, pagedResponseResult.getPageSize());
        assertEquals(page, pagedResponseResult.getPageNumber());
    }

   @Test
    void testgetQuestionsAscPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        when(questionRepository.findAllByUserId(USER_ID, pageable)).thenReturn(questionPage);

        questionService.getQuestions(page, size , "question,asc", USER_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(questionRepository).findAllByUserId(eq(USER_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(order.isAscending());
    }

    @Test
    void testQuestionsDescPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        when(questionRepository.findAllByUserId(USER_ID, pageable)).thenReturn(questionPage);

        questionService.getQuestions(page, size , "question,desc", USER_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(questionRepository).findAllByUserId(eq(USER_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(!order.isAscending());
    }

    @MockBean(QuestionRepository.class)
    QuestionRepository questionRepository() {
        return mock(QuestionRepository.class);
    }
}
