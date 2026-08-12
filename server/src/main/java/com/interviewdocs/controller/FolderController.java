package com.interviewdocs.controller;

import java.util.List;
import java.util.Set;

import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import com.interviewdocs.model.*;
import com.interviewdocs.services.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;

import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/folders")
public class FolderController {
    private final FolderService folderService;

    FolderController(FolderService folderService) {
        this.folderService = folderService;
    }
    
    @Get("/all")
    List<Folder> all(Principal auth) {
        return folderService.getAllFolders(auth);
    }
    
    @Get
    PagedResponse<Folder> getFolders(Principal auth, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size, @QueryValue(value = "field", defaultValue = "viewedAt") String field, @QueryValue(value = "direction", defaultValue = "desc") String direction) {
        String sort = field + "," + direction;
        return folderService.getFolders(page, size, sort, auth.getName());
    }
    
    @Post
    HttpResponse<Folder> newFolder(@Body Folder newFolder) {
        return HttpResponse.ok(folderService.saveFolder(newFolder));
    }

    @Get("/{id:[0-9]+}")
    HttpResponse<Folder> one(@PathVariable("id") Long id) {
        return HttpResponse.ok(folderService.getFolder(id));
    }

    @Put("/{id}")
    void replaceFolder(@Body Folder newFolder, @PathVariable("id") Long id) {
        folderService.addFolder(newFolder, id);
    }

    @Delete("/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
       folderService.deleteFolder(id);
    } 

    @Get("/{id}/questions")
    PagedResponse<Question> getQuestionsFromFolder(Principal auth, @PathVariable("id") Long id, @QueryValue(value = "page", defaultValue = "0") int page, 
    @QueryValue(value="size", defaultValue = "10") int size, @QueryValue(value = "field", defaultValue = "viewedAt") String field, 
    @QueryValue(value="direction", defaultValue = "desc") String direction) {
        String sort = field + "," + direction;
        return folderService.getQuestionsInFolder(page, size, sort, auth.getName(), id);
    }

    @Get("/{id}/questions/all")
    public Set<Question> getAllQuestionsFromFolder(@PathVariable("id") Long id) {
        return folderService.getAllQuestionsInFolder(id);
    }
    

    @Post("/{id}/questions/add")
    public void postQuestionToFolder(@PathVariable("id") Long folderId, @Body Long questionId) {
        folderService.postQuestionToFolder(folderId, questionId);
    }
    
    @Delete("/{folderId}/questions/{questionId}/delete")
    public void deleteQuestionInFolder(@PathVariable("folderId") Long folderId, @PathVariable("questionId") Long questionId) {
        folderService.deleteQuestionInFolder(folderId, questionId);
    }
}
