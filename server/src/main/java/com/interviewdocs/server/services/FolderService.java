package com.interviewdocs.server.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.interviewdocs.server.error.FolderNotFoundException;
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.FolderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    public Page<Folder> getFolders(int page, int size, String sort, String userId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(field).descending());
        }
        else {
            pageable = PageRequest.of(page, size, Sort.by(field).ascending());
        }
        
        List<Folder> folders = folderRepository.findAll();
        folders.removeIf(folder -> !folder.getUserId().equals(userId));

        int toIndex = (page + 1) * size > folders.size() ? folders.size() : (page + 1) * size;
        
        return new PageImpl<>(folders.subList(page * size, toIndex), pageable, folders.size());
    }

     public Page<Question> getQuestionsInFolder(int page, int size, String sort, String userId, Long folderId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(field).descending());
        }
        else {
            pageable = PageRequest.of(page, size, Sort.by(field).ascending());
        }
        
        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));


        Set<Question> questions = folder.getQuestions();
        questions.removeIf(question -> !question.getUserId().equals(userId));

        int toIndex = (page + 1) * size > questions.size() ? questions.size() : (page + 1) * size;
        
        return new PageImpl<>(questions.stream().toList().subList(page * size, toIndex), pageable, questions.size());
    }
}
