package com.interviewdocs.server.controller;

import com.interviewdocs.server.services.FolderService;
import com.interviewdocs.server.services.QuestionService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

@RestController
public class FolderController {
    private final FolderRepository folderRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    private FolderService folderService;

    FolderController(FolderRepository folderRepository, QuestionRepository questionRepository) {
        this.folderRepository = folderRepository;
        this.questionRepository = questionRepository;
    }
    
    @GetMapping("/folders")
    PagedModel<Folder> getFolders(Authentication auth, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewed_at,desc") String sort) {
        if (auth.isAuthenticated()) {
            return new PagedModel<>(folderService.getFolders(page, size, sort, auth.getName()));
        }

        return null;
    }
      
    
    @PostMapping("/folders")
    ResponseEntity<Folder> newFolder(@RequestBody Folder newFolder, Authentication auth) {
        if (auth.isAuthenticated() && newFolder.getUserId().equals(auth.getName())) {
            return ResponseEntity.ok(folderRepository.save(newFolder));
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/folders/{id}")
    ResponseEntity<Folder> one(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Folder folder = folderRepository.findById(id)
            .orElseThrow(() -> new FolderNotFoundException(id));
        
            return ResponseEntity.ok(folder);
        }   

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/folders/{id}")
    void replaceFolder(Authentication auth, @RequestBody Folder newFolder, @PathVariable("id") Long id) {
        if (auth.isAuthenticated() && newFolder.getUserId().equals(auth.getName())) {    
            folderRepository.findById(id)
            .map(folder -> {
                if (newFolder.getTitle() != null && !newFolder.getTitle().equals(newFolder.getTitle())) {
                    folder.setTitle(newFolder.getTitle());
                    folder.setEditedAt(newFolder.getEditedAt());
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
    void deleteQuestion(Authentication auth, @PathVariable("id") Long id) {
        if (auth.isAuthenticated()) {
            folderRepository.deleteById(id);
        }
    } 
    
    @GetMapping("/folders/{id}/questions")
    PagedModel<Question> getQuestionsFromFolder(Authentication auth, @PathVariable("id") Long id, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewed_at, desc") String sort) {
        if (auth.isAuthenticated()) {
          
            return new PagedModel<>(folderService.getQuestionsInFolder(page, size, sort, auth.getName(), id));
        }

        return null;
    }

    @PostMapping("/folders/{id}/questions/add")
    public void postQuestionToFolder(@PathVariable("id") Long folderId, @RequestBody Long questionId, Authentication auth) {
        if (auth.isAuthenticated()) {
            Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new FolderNotFoundException(folderId));

            Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

            folder.addQuestion(question);
            folderRepository.save(folder);
        }
    }
    
    @DeleteMapping("/folders/{folderId}/questions/{questionId}/delete")
    public void deleteQuestionInFolder(Authentication auth, @PathVariable("folderId") Long folderId, @PathVariable("questionId") Long questionId) {
        if (auth.isAuthenticated()) {
            Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new FolderNotFoundException(folderId));

            Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));
            
            folder.removeQuestion(question);
            folderRepository.save(folder);
        }
    }
}
