package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        new Question(USER_ID, "orange?"),
        new Question(USER_ID, "pineapple?"),
        new Question(USER_ID, "carrot?"),
        new Question(USER_ID, "potato?")
    );

    final List<Folder> folders = List.of(
        new Folder(USER_ID, "fruits"),
        new Folder(USER_ID, "vegetables")
    );

    @Test
    void testgetFoldersAscSort() {
        String sort = "title, asc";
        int page = 0, size = folders.size();

        folderRepository.insertAll(folders);

        PagedResponse<Folder> folderPage = folderService.getFolders(page, size, sort, USER_ID);
        
        List<String> expected = List.of("fruits", "vegetables");
        List<String> result = folderPage.getContent().stream().map(folder -> folder.getTitle()).toList();

        assertEquals(expected, result);
        assertEquals(page, folderPage.getPageNumber());
        assertEquals(size, folderPage.getTotalSize());
        assertEquals(size, folderPage.getPageSize());
    }

    @Test
    void testgetQuestionsInFoldersAscSort() {
        String sort = "question, asc";
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

        PagedResponse<Question> fruitsPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, fruitFolder.getId());
        PagedResponse<Question> veggiesPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, veggieFolder.getId());
        
        List<String> fruitExpected = List.of("orange?", "pineapple?");
        List<String> veggieExpected = List.of("carrot?", "potato?");

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
    void testgetFoldersDescSort() {
        String sort = "title, desc";
        int page = 0, size = folders.size();

        folderRepository.insertAll(folders);

        PagedResponse<Folder> folderPage = folderService.getFolders(page, size, sort, USER_ID);
        
        List<String> expected = List.of("vegetables", "fruits");
        List<String> result = folderPage.getContent().stream().map(folder -> folder.getTitle()).toList();

        assertEquals(expected, result);
        assertEquals(page, folderPage.getPageNumber());
        assertEquals(size, folderPage.getTotalSize());
        assertEquals(size, folderPage.getPageSize());
    }

    @Test
    void testgetQuestionsInFoldersDescSort() {
        String sort = "question, desc";
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

        PagedResponse<Question> fruitsPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, fruitFolder.getId());
        PagedResponse<Question> veggiesPage = folderService.getQuestionsInFolder(page, size, sort, USER_ID, veggieFolder.getId());
        
        List<String> fruitExpected = List.of("pineapple?", "orange?");
        List<String> veggieExpected = List.of("potato?", "carrot?");

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
