package com.interviewdocs.services;

import io.micronaut.context.BeanProvider;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import com.interviewdocs.utils.PagedResponse;
import com.interviewdocs.utils.Utils;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import com.interviewdocs.error.QuestionNotFoundException;
import com.interviewdocs.model.Folder;
import com.interviewdocs.model.Question;
import com.interviewdocs.model.Video;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.repository.VideoRepository;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class QuestionService extends Utils {
    
    private BeanProvider<QuestionRepository> questionRepositoryProvider;
    private S3Service s3Service;
    private BeanProvider<VideoRepository> videoRepositoryProvider;

    public QuestionService(BeanProvider<QuestionRepository> questionRepositoryProvider, S3Service s3Service, 
        BeanProvider<VideoRepository> videoRepositoryProvider) {
        this.questionRepositoryProvider = questionRepositoryProvider;
        this.s3Service = s3Service;
        this.videoRepositoryProvider = videoRepositoryProvider;
    }

    public List<Question> getAllQuestions(Principal auth) {
        return questionRepositoryProvider.get().findAllByUserId(auth.getName());
    }

    public Question saveQuestion(Question question) {
        return questionRepositoryProvider.get().save(question);
    }

    public Question getQuestion(Long id) throws QuestionNotFoundException {
        return questionRepositoryProvider.get().findById(id)
        .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    public void putQuestion(Long id, Question newQuestion) {
        QuestionRepository questionRepository = questionRepositoryProvider.get();

        questionRepository.findById(id)
            .map(question -> {
                question.setEverything(newQuestion);
                return questionRepository.save(question);
            })
            .orElseGet(() -> {
                return questionRepository.save(newQuestion);
            });
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question question = getQuestion(id);
        
        Set<Video> videos = question.getVideos();

        for (Video v : videos) {
            s3Service.deleteS3Object(v.getKeyName());
        }
        
        videoRepositoryProvider.get().deleteByQuestion(question);
        questionRepositoryProvider.get().deleteById(id);
    }

    public Set<Folder> getFoldersContainingQuestion(Long id) {
        return getQuestion(id).getFolders();
    }

    public Page<Question> findByUserIdAndIdIn(String userId, Set<Long> questionIds, Pageable pageable) {
        return questionRepositoryProvider.get().findByUserIdAndIdIn(userId, questionIds, pageable);
    }

    public PagedResponse<Question> getQuestions(int page, int size, String sort, String userId) {
        String[] sortOptions = sort.split(",");

        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = getPageable(page, size, field, direction);

        Page<Question> questionPage = questionRepositoryProvider.get().findAllByUserId(userId, pageable);

        return new PagedResponse<Question>(
            questionPage.getContent(),
            questionPage.getTotalSize(),
            questionPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
