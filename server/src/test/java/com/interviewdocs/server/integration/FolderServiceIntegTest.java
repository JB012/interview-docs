package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.interviewdocs.model.Question;
import com.interviewdocs.model.Folder;
import com.interviewdocs.repository.FolderRepository;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.services.FolderService;
import com.interviewdocs.utils.PagedResponse;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "test")
public class FolderServiceIntegTest {
    @Inject
    FolderRepository folderRepository;

    @Inject
    QuestionRepository questionRepository;

    @Inject
    FolderService folderService;

    final static String USER_ID = "1234";

    final List<Question> questions = List.of(
        new Question(USER_ID, "orange?", OffsetDateTime.parse("2026-07-20T08:36:10Z"), 
        OffsetDateTime.parse("2026-07-21T16:09:50Z")),
        new Question(USER_ID, "pineapple?", OffsetDateTime.parse("2026-06-08T11:09:57Z"), 
        OffsetDateTime.parse("2026-06-09T12:04:23Z")),
        new Question(USER_ID, "carrot?", OffsetDateTime.parse("2026-05-12T15:43:10Z"), 
        OffsetDateTime.parse("2026-05-13T01:34:38Z")),
        new Question(USER_ID, "potato?", OffsetDateTime.parse("2026-07-21T09:51:08Z"), 
        OffsetDateTime.parse("2026-07-21T10:42:44Z"))
    );

    final List<Folder> folders = List.of(
        new Folder(USER_ID, "fruits", OffsetDateTime.parse("2005-12-01T23:12:02Z"), 
        OffsetDateTime.parse("2005-12-02T03:12:02Z")),
        new Folder(USER_ID, "vegetables", OffsetDateTime.parse("2012-09-21T10:12:06Z"), 
        OffsetDateTime.parse("2012-09-22T01:01:56Z"))
    );

    void testgetFoldersSortHelper(int page, int size, String sort, List<String> expected) {
        PagedResponse<Folder> folderPage = folderService.getFolders(page, size, sort, USER_ID);
        
        List<String> result = folderPage.getContent().stream().map(folder -> folder.getTitle()).toList();

        assertEquals(expected, result);
        assertEquals(page, folderPage.getPageNumber());
        assertEquals(size, folderPage.getTotalSize());
        assertEquals(size, folderPage.getPageSize());
    }

    @Test
    void testgetFoldersSort() {
        int page = 0, size = folders.size();

        folderRepository.insertAll(folders);

        testgetFoldersSortHelper(page, size, "title, asc", List.of("fruits", "vegetables"));
        testgetFoldersSortHelper(page, size, "title, desc", List.of("vegetables", "fruits"));

        testgetFoldersSortHelper(page, size, "editedAt, asc", List.of("fruits", "vegetables"));
        testgetFoldersSortHelper(page, size, "editedAt, desc", List.of("vegetables", "fruits"));
        
        testgetFoldersSortHelper(page, size, "viewedAt, asc",  List.of("fruits", "vegetables"));
        testgetFoldersSortHelper(page, size, "viewedAt, desc",  List.of("vegetables", "fruits"));
    }

    void testGetQuestionsInFolderSortHelper(int page, int size, String sort, Long fruitId, Long veggieId, 
        List<String> fruitExpected, List<String> veggieExpected) {
        PagedResponse<Question> fruitsPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, fruitId);
        PagedResponse<Question> veggiesPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, veggieId);

        List<String> fruitResult = fruitsPage.getContent().stream().map(question -> question.getQuestion()).toList();
        List<String> veggieResult = veggiesPage.getContent().stream().map(question -> question.getQuestion()).toList();
        
        assertEquals(fruitExpected, fruitResult);
        assertEquals(page, fruitsPage.getPageNumber());
        assertEquals(size/2, fruitsPage.getTotalSize());
        assertEquals(size/2, fruitsPage.getPageSize());
        
        assertEquals(veggieExpected, veggieResult);
        assertEquals(page, veggiesPage.getPageNumber());
        assertEquals(size/2, veggiesPage.getTotalSize());
        assertEquals(size/2, veggiesPage.getPageSize());
    }

    @Test
    void testgetQuestionsInFolderQuestionSort() {
        int page = 0, size = questions.size();

        questionRepository.insertAll(questions);
        folderRepository.insertAll(folders);

        List<Folder> folders = folderRepository.findAllByUserId(USER_ID);
        
        Folder fruitFolder = folders.get(0);

        fruitFolder.addQuestion(questions.get(0));
        fruitFolder.addQuestion(questions.get(1));

        Folder veggieFolder = folders.get(1);

        veggieFolder.addQuestion(questions.get(2));
        veggieFolder.addQuestion(questions.get(3));

        testGetQuestionsInFolderSortHelper(page, size, "question, asc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("orange?", "pineapple?"), List.of("carrot?", "potato?"));
        testGetQuestionsInFolderSortHelper(page, size, "question, desc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("pineapple?", "orange?"), List.of("potato?", "carrot?"));

        testGetQuestionsInFolderSortHelper(page, size, "editedAt, asc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("pineapple?", "orange?"), List.of("carrot?", "potato?"));
        testGetQuestionsInFolderSortHelper(page, size, "editedAt, desc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("orange?", "pineapple?"), List.of( "potato?", "carrot?"));

        testGetQuestionsInFolderSortHelper(page, size, "viewedAt, asc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("pineapple?", "orange?"), List.of("carrot?", "potato?"));
        testGetQuestionsInFolderSortHelper(page, size, "viewedAt, desc", fruitFolder.getId(), veggieFolder.getId(), 
        List.of("orange?", "pineapple?"), List.of("potato?", "carrot?"));
    }

    @Test
    void testgetFoldersSizeLimit() {
        String sort = "title, asc";
        int pageSize = 1;
        
        folderRepository.insertAll(folders);

        for (int i = 0; i < folders.size(); i++) {
            PagedResponse<Folder> folderPage = folderService.getFolders(i, pageSize, sort, USER_ID);

            assertEquals(List.of(folders.get(i)), folderPage.getContent());
            assertEquals(pageSize, folderPage.getPageSize());
            assertEquals(folders.size(), folderPage.getTotalSize());
            assertEquals(i, folderPage.getPageNumber());
        }
    }

    @Test
    void testgetQuestionsInFolderSizeLimit() {
        String sort = "question, asc";
        int pageSize = 1;

        questionRepository.insertAll(questions);
        folderRepository.insertAll(folders);

        Folder fruitFolder = folders.get(0);
        Folder veggieFolder = folders.get(1);

        fruitFolder.addQuestion(questions.get(0));
        fruitFolder.addQuestion(questions.get(1));
        
        veggieFolder.addQuestion(questions.get(2));
        veggieFolder.addQuestion(questions.get(3));

        for (int i = 0; i < questions.size() / 2; i++) {
            PagedResponse<Question> fruitsPage = folderService.getQuestionsInFolder(i, pageSize, sort, USER_ID, fruitFolder.getId());

            assertEquals(List.of(questions.get(i)), fruitsPage.getContent());
            assertEquals(pageSize, fruitsPage.getPageSize());
            assertEquals(questions.size() / 2, fruitsPage.getTotalSize());
            assertEquals(i, fruitsPage.getPageNumber());
        }   

        for (int i = questions.size() / 2; i < questions.size(); i++) {
            PagedResponse<Question> veggiesPage = folderService.getQuestionsInFolder(i - (questions.size() / 2), pageSize, sort, USER_ID, veggieFolder.getId());

            assertEquals(List.of(questions.get(i)), veggiesPage.getContent());
            assertEquals(pageSize, veggiesPage.getPageSize());
            assertEquals(questions.size() / 2, veggiesPage.getTotalSize());
            assertEquals(i - (questions.size() / 2), veggiesPage.getPageNumber());
        }
    }
}
