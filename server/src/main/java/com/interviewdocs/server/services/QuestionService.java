package com.interviewdocs.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.QuestionRepository;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    public Page<Question> getQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findAll(pageable);
    }
}
