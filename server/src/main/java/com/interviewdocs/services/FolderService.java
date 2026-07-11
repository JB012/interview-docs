package com.interviewdocs.server.services;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import com.interviewdocs.server.utils.PagedResponse;
import io.micronaut.data.model.Sort;
import org.springframework.stereotype.Service;

import com.interviewdocs.server.error.FolderNotFoundException;
import com.interviewdocs.server.model.Folder;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.FolderRepository;
import com.interviewdocs.server.repository.QuestionRepository;


@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public PagedResponse<Folder> getFolders(int page, int size, String sort, String userId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.equals("desc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        }
        else {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        }

        Page<Folder> folderPage = folderRepository.findAllByUserId(userId, pageable);
        
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

        Pageable pageable = null;

        if (direction.equals("desc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        }
        else {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        }

        Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new FolderNotFoundException(folderId));

        Set<Long> questionIds = folder.getQuestions().stream()
        .map(Question::getId)
        .collect(Collectors.toSet());

        Page<Question> questionPage = questionRepository.findByUserIdAndIdIn(userId, questionIds, pageable);

        return new PagedResponse<Question>(
            questionPage.getContent(),
            questionPage.getTotalSize(),
            questionPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
