package com.interviewdocs.server.controller;

import com.interviewdocs.server.services.FolderService;
import com.interviewdocs.server.services.QuestionService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import io.micronaut.data.model.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewdocs.server.error.FolderNotFoundException;
import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.FolderRepository;
import com.interviewdocs.server.repository.QuestionRepository;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;

import com.interviewdocs.server.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@RestController
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
    
    @GetMapping("/folders/all")
    List<Folder> getAllFolders(Principal auth) {
        return folderRepository.findAllByUserId(auth.getName());
    }
    
    @GetMapping("/folders")
    PagedResponse<Folder> getFolders(Principal auth, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewedAt,desc") String sort) {
        return folderService.getFolders(page, size, sort, auth.getName());
    }
    
    @PostMapping("/folders")
    ResponseEntity<Folder> newFolder(@RequestBody Folder newFolder, Principal auth) {
        if (newFolder.getUserId().equals(auth.getName())) {
            return ResponseEntity.ok(folderRepository.save(newFolder));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/folders/{id:[0-9]+}")
    ResponseEntity<Folder> one(@PathVariable("id") Long id) {
        Folder folder = folderRepository.findById(id)
        .orElseThrow(() -> new FolderNotFoundException(id));
    
        return ResponseEntity.ok(folder);
    }

    @PutMapping("/folders/{id}")
    void replaceFolder(Principal auth, @RequestBody Folder newFolder, @PathVariable("id") Long id) {
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

    @DeleteMapping("/folders/{id}")
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

    @GetMapping("/folders/{id}/questions")
    PagedResponse<Question> getQuestionsFromFolder(Principal auth, @PathVariable("id") Long id, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewedAt, desc") String sort) {
        return folderService.getQuestionsInFolder(page, size, sort, auth.getName(), id);
    }

    @GetMapping("/folders/{id}/questions/all")
    public Set<Question> getAllQuestionsFromFolder(Principal auth, @PathVariable("id") Long id) {
        Folder folder = folderRepository.findById(id)
            .orElseThrow(() -> new FolderNotFoundException(id));
        
        return folder.getQuestions();
    }
    

    @PostMapping("/folders/{id}/questions/add")
    public void postQuestionToFolder(@PathVariable("id") Long folderId, @RequestBody Long questionId) {
        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));

        Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new QuestionNotFoundException(questionId));

        folder.addQuestion(question);

        folderRepository.save(folder);
        questionRepository.save(question);
    }
    
    @DeleteMapping("/folders/{folderId}/questions/{questionId}/delete")
    public void deleteQuestionInFolder(@PathVariable("folderId") Long folderId, @PathVariable("questionId") Long questionId) {
        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));

        Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new QuestionNotFoundException(questionId));
        
        System.out.println(question.getFolders().contains(folder));
        System.out.println(folder.getQuestions().contains(question));
    
        Folder folderInSet = question.getFolders().iterator().next();

        System.out.println(folderInSet.getId().equals(folder.getId()));
        
        System.out.println(folderInSet.equals(folder));
        question.removeFromFolder(folder);
        
        System.out.println(folder.getQuestions().size());
        System.out.println(question.getFolders().size());
        
        folderRepository.save(folder);
        questionRepository.save(question);
    }
}
