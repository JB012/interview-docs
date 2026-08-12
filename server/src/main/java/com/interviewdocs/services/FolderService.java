package com.interviewdocs.services;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.micronaut.context.BeanProvider;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import com.interviewdocs.utils.PagedResponse;
import com.interviewdocs.utils.Utils;
import com.interviewdocs.error.FolderNotFoundException;
import com.interviewdocs.model.Folder;
import com.interviewdocs.model.Question;
import com.interviewdocs.repository.FolderRepository;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class FolderService extends Utils {
    private final BeanProvider<FolderRepository> folderRepositoryProvider;
    private final QuestionService questionService;

    public FolderService(BeanProvider<FolderRepository> folderRepositoryProvider, 
        QuestionService questionService) {
        this.folderRepositoryProvider = folderRepositoryProvider;
        this.questionService = questionService;
    }

    public Folder getFolder(Long folderId) throws FolderNotFoundException {
        return folderRepositoryProvider.get().findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));
    }

    public Folder saveFolder(Folder newFolder) {
        return folderRepositoryProvider.get().save(newFolder);
    }

    public Set<Question> getAllQuestionsInFolder(Long id) {
        return getFolder(id).getQuestions();
    }

    public List<Folder> getAllFolders(Principal auth) {
        return folderRepositoryProvider.get().findAllByUserId(auth.getName());
    }

    public void addFolder(Folder newFolder, Long id) {
        folderRepositoryProvider.get().findById(id)
        .map(folder -> {
            if (newFolder.getTitle() != null && !newFolder.getTitle().equals(folder.getTitle())) {
                folder.setTitle(newFolder.getTitle());
                folder.setEditedAt(OffsetDateTime.now());
            }
            else {
                folder.setTime(newFolder);
            }

            return saveFolder(folder);
        })
        .orElseGet(() -> {
            return saveFolder(newFolder);
        });
    }

    @Transactional
    public void deleteFolder(Long id) {
        Folder folder = getFolder(id);
        Set<Question> questions = folder.getQuestions();

        for (Question q : questions) {
            Long questionId = q.getId();
            // Retrieved from repository to ensure session.
            Question question = questionService.getQuestion(questionId);
        
            question.getFolders().remove(folder);
            questionService.saveQuestion(question);
        }

        folder.clearFolder();

        folderRepositoryProvider.get().deleteById(id);
    }

    @Transactional
    public void postQuestionToFolder(Long folderId, Long questionId) {
        Folder folder = getFolder(folderId);

        Question question = questionService.getQuestion(questionId);

        folder.addQuestion(question);
        saveFolder(folder);
        questionService.getQuestion(questionId);
    }

    @Transactional
    public void deleteQuestionInFolder(Long folderId, Long questionId) {
        Folder folder = getFolder(folderId);

        Question question = questionService.getQuestion(questionId);

        question.removeFromFolder(folder);
    
        saveFolder(folder);
        questionService.saveQuestion(question);
    }

    public PagedResponse<Folder> getFolders(int page, int size, String sort, String userId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = getPageable(page, size, field, direction);

        Page<Folder> folderPage = folderRepositoryProvider.get().findAllByUserId(userId, pageable);
        
        return new PagedResponse<Folder>(
            folderPage.getContent(),
            folderPage.getTotalSize(),
            folderPage.getContent().size(),
            pageable.getNumber()
        );
    }

    public PagedResponse<Question> getQuestionsInFolder(int page, int size, String sort, String userId, Long folderId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = getPageable(page, size, field, direction);

        Folder folder = getFolder(folderId);

        Set<Long> questionIds = folder.getQuestions().stream()
        .map(Question::getId)
        .collect(Collectors.toSet());

        Page<Question> questionPage = questionService.findByUserIdAndIdIn(userId, questionIds, pageable);

        return new PagedResponse<Question>(
            questionPage.getContent(),
            questionPage.getTotalSize(),
            questionPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
