package com.interviewdocs.services;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import io.micronaut.context.BeanProvider;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.multipart.CompletedFileUpload;

import com.interviewdocs.utils.PagedResponse;
import com.interviewdocs.utils.Utils;
import com.interviewdocs.error.QuestionNotFoundException;
import com.interviewdocs.error.VideoNotFoundException;
import com.interviewdocs.model.Question;
import com.interviewdocs.model.Video;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.repository.VideoRepository;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class VideoService extends Utils {
    private final S3Service s3Service;
    private final BeanProvider<VideoRepository> videoRepositoryProvider;
    private final BeanProvider<QuestionRepository> questionRepositoryProvider;
    
    public VideoService(S3Service s3Service, BeanProvider<VideoRepository> videoRepositoryProvider, BeanProvider<QuestionRepository> questionRepositoryProvider) {
        this.s3Service = s3Service;
        this.videoRepositoryProvider = videoRepositoryProvider;
        this.questionRepositoryProvider = questionRepositoryProvider;
    }

    public void setSourceToSignedURL(Video video) throws Exception {
        Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);

        String signedURL = s3Service.createSignedUrl(video.getKeyName(), expiration, "SHA256");
        video.setSource(signedURL);
    }

    public String getUserIdNumber(String userId) {
        // Auth0 User ID : {connection}|{userIdNumber}. 
        // Connection is irrelevant and the vertical bar character isn't recommended for S3 naming convention
        return userId.split("\\|")[1];
    }

    public Video getVideo(Long id) {
        Video video = videoRepositoryProvider.get().findById(id)
        .orElseThrow(() -> new VideoNotFoundException(id));

        try {
            setSourceToSignedURL(video);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return video;
    }

    public Video saveVideo(Video video) {
        return videoRepositoryProvider.get().save(video);
    }

    @Transactional
    public Video postVideo(Video newVideo, Long questionId) {
        Question question = questionRepositoryProvider.get().findById(questionId)
        .orElseThrow(() -> new QuestionNotFoundException(questionId));

        question.addVideo(newVideo);
        newVideo.addQuestion(question);

        videoRepositoryProvider.get().save(newVideo);
        
        try {
            String preSignedURL = s3Service.createPresignedPutUrl(newVideo.getKeyName());
            newVideo.setSource(preSignedURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newVideo;
    }

    public void putVideo(Long id, Video newVideo) {
        Video video = videoRepositoryProvider.get().findById(id)
        .orElse(newVideo);

        if (newVideo.getTitle() != null && !newVideo.getTitle().equals(video.getTitle())) { 
            s3Service.changeObjectName(video.getKeyName(), newVideo.getKeyName());
            
            video.setTitle(newVideo.getTitle());
            video.setEditedAt(OffsetDateTime.now());
        }
        else {
            video.setTime(newVideo);
        }
        
        saveVideo(video);
    }

    public void deleteVideo(Long id) {
        Video video = getVideo(id);

        s3Service.deleteS3Object(video.getKeyName());

        videoRepositoryProvider.get().deleteById(id);
    }

    public void deleteByQuestion(Question question) {
        videoRepositoryProvider.get().deleteByQuestion(question);
    }
    
    public PagedResponse<Video> getVideos(int page, int size, String sort, String userId, Long questionId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = getPageable(page, size, field, direction);

        Page<Video> videoPage = videoRepositoryProvider.get().findAllByUserIdAndQuestionId(userId, questionId, pageable);

        videoPage.map((video) -> {
            try { 
                setSourceToSignedURL(video);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            return video;
        });

        return new PagedResponse<Video>(
            videoPage.getContent(),
            videoPage.getTotalSize(),
            videoPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
