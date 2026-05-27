package com.interviewdocs.server.controller;

import com.interviewdocs.server.services.FolderService;

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
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.repository.FolderRepository;

@RestController
public class FolderController {
    private final FolderRepository repository;

    @Autowired
    private FolderService folderService;

    FolderController(FolderRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping("/folders")
    PagedModel<Folder> getFolders(Authentication auth, @RequestParam(name = "page", defaultValue = "0") int page, 
    @RequestParam(name="size", defaultValue = "10") int size, @RequestParam(name = "sort", defaultValue = "viewed_at, desc") String sort) {
        if (auth.isAuthenticated()) {
            return new PagedModel<>(folderService.getFolders(page, size, sort, auth.getName()));
        }

        return null;
    }
      
    @PostMapping("/folders")
    ResponseEntity<String> newFolder(@RequestBody Folder newFolder, Authentication auth) {
        if (auth.isAuthenticated() && newFolder.getUserId().equals(auth.getName())) {
            repository.save(newFolder);

            return ResponseEntity.ok().build();
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/folders/{id}")
    ResponseEntity<Folder> one(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Folder folder = repository.findById(id)
            .orElseThrow(() -> new FolderNotFoundException(id));
        
            return ResponseEntity.ok(folder);
        }   

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/folders/{id}")
    void replaceFolder(Authentication auth, @RequestBody Folder newFolder, @PathVariable("id") Long id) {
        if (auth.isAuthenticated() && newFolder.getUserId().equals(auth.getName())) {    
            repository.findById(id)
            .map(folder -> {
                folder.setName(newFolder.getName());
                return repository.save(folder);
            })
            .orElseGet(() -> {
                return repository.save(newFolder);
            });
        }
    }

    @DeleteMapping("/folders/{id}")
    void deleteQuestion(Authentication auth, @PathVariable("id") Long id) {
        if (auth.isAuthenticated()) {
            repository.deleteById(id);
        }
    } 
}
