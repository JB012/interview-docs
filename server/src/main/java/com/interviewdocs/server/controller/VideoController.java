package com.interviewdocs.server.controller;

import com.interviewdocs.server.services.S3Service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.VideoNotFoundException;
import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.VideoService;

@RestController
public class VideoController {
    private final S3Service s3Service;

    private final VideoRepository repository;
    
    @Autowired
    private VideoService videoService;

    VideoController(VideoRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    @GetMapping("/videos")
    List<Video> all() {
        List<Video> videoList = repository.findAll();

        for (int i = 0; i < videoList.size(); i++) {
            try {
                videoService.setSourceToPresignedURL(videoList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return videoList;
     
    }

    @PostMapping("/videos")
    Video newVideo(@RequestBody Video newVideo) {
        try {
            videoService.setSourceToPresignedURL(newVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return repository.save(newVideo);
    }

    @GetMapping("/videos/{id}")
    Video one(@PathVariable("id") Long id) {
        Video video = repository.findById(id)
        .orElseThrow(() -> new VideoNotFoundException(id));

        try {
            videoService.setSourceToPresignedURL(video);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return video;
    }

    @PutMapping("/videos/{id}")
    Video replaceQuestion(@RequestBody Video newVideo, @PathVariable("id") Long id) {
        
        return repository.findById(id)
        .map(video -> {
            String keyName = video.getUserId() + "/" + video.getTitle();
            String newKeyName = newVideo.getUserId() + "/" + newVideo.getTitle();
            s3Service.changeObjectName("interviewdocs-videos", keyName, newKeyName);

            video.setTitle(newVideo.getTitle());
            
            try {
                videoService.setSourceToPresignedURL(video);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return repository.save(video);
        })
        .orElseGet(() -> {
            try {
                videoService.setSourceToPresignedURL(newVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return repository.save(newVideo);
        });
    }

    @DeleteMapping("/videos/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
    
    
}
