package com.interviewdocs.server.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.interviewdocs.model.Folder;
import com.interviewdocs.model.Question;
import com.interviewdocs.repository.FolderRepository;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.services.FolderService;
import com.interviewdocs.utils.PagedResponse;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "test")
public class FolderServiceUnitTest {
    @Inject
    QuestionRepository questionRepository;

    @Inject
    FolderRepository folderRepository;

    @Inject
    FolderService folderService;

    final static String USER_ID = "1234";

    
    final List<Folder> folders = List.of(
        new Folder(1L, USER_ID, "folder1"),
        new Folder(2L, USER_ID, "folder2")
    );

    final List<Question> questions = List.of(
        new Question(3L, USER_ID, "?"),
        new Question(1L, USER_ID, "??"),
        new Question(2L, USER_ID, "???")
    );

    final int page = 0;
    final int size = folders.size();
    final String field = "title";

    @Test
    void testGetFoldersPagedResponse() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Folder> folderPage = Page.of(folders, pageable, Long.valueOf(size));
        
        when(folderRepository.findAllByUserId(USER_ID, pageable)).thenReturn(folderPage);
        
        PagedResponse<Folder> pagedResponseResult = folderService.getFolders(page, size, "title,asc", USER_ID);

        assertEquals(folders, pagedResponseResult.getContent());
        assertEquals(size, pagedResponseResult.getTotalSize());
        assertEquals(size, pagedResponseResult.getPageSize());
        assertEquals(page, pagedResponseResult.getPageNumber());
    }

    @Test
    void testgetFoldersAscPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Folder> folderPage = Page.of(folders, pageable, Long.valueOf(size));

        when(folderRepository.findAllByUserId(USER_ID, pageable)).thenReturn(folderPage);
    
        folderService.getFolders(page, size , "title,asc", USER_ID);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(folderRepository).findAllByUserId(eq(USER_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(order.isAscending());
    }

    @Test
    void testgetFoldersDescPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        Page<Folder> folderPage = Page.of(folders, pageable, Long.valueOf(size));

        when(folderRepository.findAllByUserId(USER_ID, pageable)).thenReturn(folderPage);
    
        folderService.getFolders(page, size , "title,desc", USER_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(folderRepository).findAllByUserId(eq(USER_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(!order.isAscending());
    }


    void testgetQuestionsInFoldersPagedResponse() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        Folder folder = folders.get(0);
        folder.setQuestions(new HashSet<Question>(questions));

        Set<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());

        when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        when(questionRepository.findByUserIdAndIdIn(USER_ID, questionIds, pageable)).thenReturn(questionPage);

        PagedResponse<Question> pagedResponseResult = folderService.getQuestionsInFolder(page, size, "title,asc", USER_ID, folder.getId());

        assertEquals(questions, pagedResponseResult.getContent());
        assertEquals(size, pagedResponseResult.getTotalSize());
        assertEquals(size, pagedResponseResult.getPageSize());
        assertEquals(page, pagedResponseResult.getPageNumber());
    }

    void testgetQuestionsInFoldersAscPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        Folder folder = folders.get(0);
        folder.setQuestions(new HashSet<Question>(questions));

        Set<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());

        when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        when(questionRepository.findByUserIdAndIdIn(USER_ID, questionIds, pageable)).thenReturn(questionPage);

        folderService.getQuestionsInFolder(page, size, "title,asc", USER_ID, folder.getId());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(questionRepository).findByUserIdAndIdIn(eq(USER_ID), questionIds, pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(order.isAscending());
    }

    void testgetQuestionsInFoldersDescPageable() {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        Page<Question> questionPage = Page.of(questions, pageable, Long.valueOf(size));

        Folder folder = folders.get(0);
        folder.setQuestions(new HashSet<Question>(questions));

        Set<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());

        when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        when(questionRepository.findByUserIdAndIdIn(USER_ID, questionIds, pageable)).thenReturn(questionPage);

        folderService.getQuestionsInFolder(page, size, "title,desc", USER_ID, folder.getId());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(questionRepository).findByUserIdAndIdIn(eq(USER_ID), questionIds, pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(order.isAscending());
    }

    @MockBean(QuestionRepository.class)
    QuestionRepository questionRepository() {
        return mock(QuestionRepository.class);
    }

    @MockBean(FolderRepository.class)
    FolderRepository folderRepository() {
        return mock(FolderRepository.class);
    }
}
