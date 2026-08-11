package com.interviewdocs.controller;

import com.interviewdocs.services.S3Service;

import java.time.OffsetDateTime;

import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;

import com.interviewdocs.repository.*;
import com.interviewdocs.error.*;
import com.interviewdocs.model.*;
import com.interviewdocs.services.VideoService;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.io.IOException;
import java.security.Principal;

import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/videos")
public class VideoController {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final QuestionRepository questionRepository;
    private final VideoService videoService;
    

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

    @Post(consumes = MediaType.MULTIPART_FORM_DATA, value = "/upload")
    boolean uploadVideo(@Part("videoBuffer") CompletedFileUpload videoBuffer) {
        try {
            return s3Service.putS3Object(videoBuffer.getFilename(), videoBuffer.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Post
    HttpResponse<Video> newVideo(@Body Video newVideo, @QueryValue(value="questionId") Long questionId) { 
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
                s3Service.changeObjectName(video.getKeyName(), newVideo.getKeyName());
                
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

        s3Service.deleteS3Object(video.getKeyName());

        videoRepository.deleteById(id);
    } 
}
