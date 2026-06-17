package com.interviewdocs.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.QuestionRepository;

@Service
public class QuestionService {
    
    private QuestionRepository questionRepository;

    QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Page<Question> getQuestions(int page, int size, String sort, String userId) {
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

        return questionRepository.findAllByUserId(userId, pageable);
    }
}
