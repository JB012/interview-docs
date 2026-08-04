package com.interviewdocs.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import com.interviewdocs.error.*;
import com.interviewdocs.model.*;
import com.interviewdocs.services.*;
import com.interviewdocs.repository.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;

import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/folders")
public class FolderController {
    private final FolderService folderService;
    private final FolderRepository folderRepository;
    private final QuestionRepository questionRepository;

    FolderController(FolderRepository folderRepository, QuestionRepository questionRepository, 
        FolderService folderService) {
        this.folderRepository = folderRepository;
        this.questionRepository = questionRepository;
        this.folderService = folderService;
    }
    
    @Get("/all")
    List<Folder> getAllFolders(Principal auth) {
        return folderRepository.findAllByUserId(auth.getName());
    }
    
    @Get
    PagedResponse<Folder> getFolders(Principal auth, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size, @QueryValue(value = "field", defaultValue = "viewedAt") String field, @QueryValue(value = "direction", defaultValue = "desc") String direction) {
        String sort = field + "," + direction;
        return folderService.getFolders(page, size, sort, auth.getName());
    }
    
    @Post
    HttpResponse<Folder> newFolder(@Body Folder newFolder, Principal auth) {
        if (newFolder.getUserId().equals(auth.getName())) {
            return HttpResponse.ok(folderRepository.save(newFolder));
        }

        return HttpResponse.noContent();
    }

    @Get("/{id:[0-9]+}")
    HttpResponse<Folder> one(@PathVariable("id") Long id) {
        Folder folder = folderRepository.findById(id)
        .orElseThrow(() -> new FolderNotFoundException(id));
    
        return HttpResponse.ok(folder);
    }

    @Put("/{id}")
    void replaceFolder(Principal auth, @Body Folder newFolder, @PathVariable("id") Long id) {
        if (newFolder.getUserId().equals(auth.getName())) {    
            folderRepository.findById(id)
            .map(folder -> {
                if (newFolder.getTitle() != null && !newFolder.getTitle().equals(folder.getTitle())) {
                    folder.setTitle(newFolder.getTitle());
                    folder.setEditedAt(OffsetDateTime.now());
                }
                else {
                    folder.setTime(newFolder);
                }

                return folderRepository.save(folder);
            })
            .orElseGet(() -> {
                return folderRepository.save(newFolder);
            });
        }
    }

    @Delete("/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        Folder folder = folderRepository.findById(id)
        .orElseThrow(() -> new FolderNotFoundException(id));

        Set<Question> questions = folder.getQuestions();

        for (Question q : questions) {
            Long questionId = q.getId();
            // Retrieved from repository to ensure session.
            Question question = questionRepository.findById(questionId)    
            .orElseThrow(() -> new QuestionNotFoundException(questionId));
        
            question.getFolders().remove(folder);
            questionRepository.save(question);
        }

        folder.clearFolder();

        folderRepository.deleteById(id);
    } 

    @Get("/{id}/questions")
    PagedResponse<Question> getQuestionsFromFolder(Principal auth, @PathVariable("id") Long id, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size, @QueryValue(value = "field", defaultValue = "viewedAt") String field, 
    @QueryValue(value="direction", defaultValue = "desc") String direction) {
        String sort = field + "," + direction;
        return folderService.getQuestionsInFolder(page, size, sort, auth.getName(), id);
    }

    @Get("/{id}/questions/all")
    public Set<Question> getAllQuestionsFromFolder(Principal auth, @PathVariable("id") Long id) {
        Folder folder = folderRepository.findById(id)
            .orElseThrow(() -> new FolderNotFoundException(id));
        
        return folder.getQuestions();
    }
    

    @Post("/{id}/questions/add")
    public void postQuestionToFolder(@PathVariable("id") Long folderId, @Body Long questionId) {
        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));

        Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new QuestionNotFoundException(questionId));

        folder.addQuestion(question);

        folderRepository.save(folder);
        questionRepository.save(question);
    }
    
    @Delete("/{folderId}/questions/{questionId}/delete")
    public void deleteQuestionInFolder(@PathVariable("folderId") Long folderId, @PathVariable("questionId") Long questionId) {
        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));

        Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new QuestionNotFoundException(questionId));
    
        Folder folderInSet = question.getFolders().iterator().next();

        question.removeFromFolder(folder);
    
        folderRepository.save(folder);
        questionRepository.save(question);
    }
}
