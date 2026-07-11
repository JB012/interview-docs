package com.interviewdocs.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import io.micronaut.data.model.Page;
import org.springframework.stereotype.Service;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;

import com.interviewdocs.server.utils.PagedResponse;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.repository.QuestionRepository;

@Service
public class QuestionService {
    
    private QuestionRepository questionRepository;

    QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public PagedResponse<Question> getQuestions(int page, int size, String sort, String userId) {
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

        Page<Question> questionPage = questionRepository.findAllByUserId(userId, pageable);

        return new PagedResponse<Question>(
            questionPage.getContent(),
            questionPage.getTotalSize(),
            questionPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
