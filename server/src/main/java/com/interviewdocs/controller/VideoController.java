package com.interviewdocs.controller;

import com.interviewdocs.services.S3Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.time.OffsetDateTime;

import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import com.interviewdocs.repository.*;
import com.interviewdocs.error.*;
import com.interviewdocs.model.*;
import com.interviewdocs.services.VideoService;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;

import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/videos")
public class VideoController {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final QuestionRepository questionRepository;
    private final VideoService videoService;
    
    private static final String BUCKET_NAME = System.getenv("VIDEO_S3_BUCKET");

    VideoController(VideoRepository videoRepository, QuestionRepository questionRepository, 
        VideoService videoService, S3Service s3Service) {
        this.videoRepository = videoRepository;
        this.questionRepository = questionRepository;
        this.videoService = videoService;
        this.s3Service = s3Service;
    }

    @Get
    PagedResponse<Video> all(Principal auth, @QueryValue(value="questionId") Long questionId, 
    @QueryValue(value = "page", defaultValue = "0") int page, @QueryValue(value="size", defaultValue = "10") int size, 
     @QueryValue(value = "field", defaultValue = "viewedAt") String field, @QueryValue(value="direction", defaultValue = "desc") String direction) {
        String id = videoService.getUserIdNumber(auth.getName());
        String sort = field + "," + direction;
        return videoService.getVideos(page, size, sort, id, questionId);
    }

    @Post
    HttpResponse<Video> newVideo(@Body Video newVideo, @QueryValue(value="questionId") Long questionId, Principal auth) { 
        if (newVideo.getUserId().equals(videoService.getUserIdNumber(auth.getName()))) {            
            Question question = questionRepository.findByQuestionId(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

            question.addVideo(newVideo);
            newVideo.addQuestion(question);
            videoRepository.save(newVideo);
            
            try {
                videoService.setSourceToPresignedURL(newVideo);
            } catch (Exception e) {
                e.printStackTrace();
                return HttpResponse.serverError();
            }

            return HttpResponse.ok(newVideo);
        }

        return HttpResponse.unauthorized();
    }

    @Get("/{id}")
    HttpResponse<Video> one(@PathVariable("id") Long id) {
        Video video = videoRepository.findById(id)
        .orElseThrow(() -> new VideoNotFoundException(id));

        try {
            videoService.setSourceToPresignedURL(video);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.serverError();
        }

        return HttpResponse.ok(video);
    }

    @Put("/{id}")
    void replaceVideo(@Body Video newVideo, @PathVariable("id") Long id) {
        videoRepository.findById(id)
        .map(video -> {
            if (newVideo.getTitle() != null && !newVideo.getTitle().equals(video.getTitle())) { 
                s3Service.changeObjectName(BUCKET_NAME, video.getKeyName(), newVideo.getKeyName());
                
                video.setTitle(newVideo.getTitle());
                video.setEditedAt(OffsetDateTime.now());
            }
            else {
                video.setTime(newVideo);
            }
            
            videoRepository.save(video);

            try {
                videoService.setSourceToPresignedURL(video);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return video;
        })
        .orElseGet(() -> {
            videoRepository.save(newVideo);

            try {
                videoService.setSourceToPresignedURL(newVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return newVideo;
        });
    }

    @Delete("/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        Video video = videoRepository.findById(id)
        .orElseThrow(() -> new VideoNotFoundException(id));

        s3Service.deleteS3Object(BUCKET_NAME, video.getKeyName());

        videoRepository.deleteById(id);
    } 
}
