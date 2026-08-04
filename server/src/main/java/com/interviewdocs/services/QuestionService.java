package com.interviewdocs.services;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;

import com.interviewdocs.utils.PagedResponse;
import com.interviewdocs.model.Question;
import com.interviewdocs.repository.QuestionRepository;

import jakarta.inject.Singleton;

@Singleton
public class QuestionService {
    
    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public PagedResponse<Question> getQuestions(int page, int size, String sort, String userId) {
        System.out.println(sort);
        String[] sortOptions = sort.split(",");
        
        for (String s : sortOptions) {
            System.out.println(s);
        }
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.trim().toLowerCase().equals("desc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        }
        else if (direction.trim().toLowerCase().equals("asc")) {
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
