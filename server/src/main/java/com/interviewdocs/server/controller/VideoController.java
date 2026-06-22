package com.interviewdocs.server.controller;

import com.interviewdocs.server.services.S3Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.QuestionNotFoundException;
import com.interviewdocs.server.error.VideoNotFoundException;
import com.interviewdocs.server.model.Question;
import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.VideoService;

@RestController
public class VideoController {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final QuestionRepository questionRepository;
    private final VideoService videoService;
    
    private static final String BUCKET_NAME = "interviewdocs-videos";

    VideoController(VideoRepository videoRepository, QuestionRepository questionRepository, 
        VideoService videoService, S3Service s3Service) {
        this.videoRepository = videoRepository;
        this.questionRepository = questionRepository;
        this.videoService = videoService;
        this.s3Service = s3Service;
    }

    @GetMapping("/videos")
    PagedModel<Video> all(Authentication auth, @RequestParam(name="questionId") String questionId, 
    @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name="size", defaultValue = "10") int size, 
    @RequestParam(name = "sort", defaultValue = "viewed_at, desc") String sort) {
        if (auth.isAuthenticated()) {
            String id = videoService.getUserIdNumber(auth.getName());

            return new PagedModel<>(videoService.getVideos(page, size, sort, id, questionId));
        }     
        
        return null;
    }

    @PostMapping("/videos")
    ResponseEntity<Video> newVideo(@RequestBody Video newVideo, @RequestParam(name="questionId") Long questionId, Authentication auth) { 
        if (auth.isAuthenticated() && newVideo.getUserId().equals(videoService.getUserIdNumber(auth.getName()))) {            
            Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

            question.addVideo(newVideo);

            videoRepository.save(newVideo);
            
            try {
                videoService.setSourceToPresignedURL(newVideo);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok(newVideo);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/videos/{id}")
    ResponseEntity<Video> one(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Video video = videoRepository.findById(id)
            .orElseThrow(() -> new VideoNotFoundException(id));

            try {
                videoService.setSourceToPresignedURL(video);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok(video);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/videos/{id}")
    void replaceVideo(@RequestBody Video newVideo, @PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            videoRepository.findById(id)
            .map(video -> {
                if (newVideo.getTitle() != null && !newVideo.getTitle().equals(video.getTitle())) { 
                    s3Service.changeObjectName(BUCKET_NAME, video.getKeyName(), newVideo.getKeyName());
                    
                    video.setTitle(newVideo.getTitle());
                    video.setEditedAt(newVideo.getEditedAt());
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
    }

    @DeleteMapping("/videos/{id}")
    void deleteQuestion(@PathVariable("id") Long id, Authentication auth) {
        if (auth.isAuthenticated()) {
            Video video = videoRepository.findById(id)
            .orElseThrow(() -> new VideoNotFoundException(id));

            Question question = video.getQuestion();

            s3Service.deleteS3Object(BUCKET_NAME, video.getKeyName());

            question.removeVideo(video);
            videoRepository.deleteById(id);
            
        }
    } 
}
