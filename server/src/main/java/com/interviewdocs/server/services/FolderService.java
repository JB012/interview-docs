package com.interviewdocs.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.interviewdocs.server.model.Folder;

import com.interviewdocs.server.repository.FolderRepository;

@Service
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
}
