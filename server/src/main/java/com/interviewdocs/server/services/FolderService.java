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
import com.interviewdocs.server.repository.QuestionRepository;

import jakarta.transaction.Transactional;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
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

        return folderRepository.findAllByUserId(userId, pageable);
    }

    @Transactional
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

        return questionRepository.findAllByUserId(userId, questions, pageable);
    }
}
