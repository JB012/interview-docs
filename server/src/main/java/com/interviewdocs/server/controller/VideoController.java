package com.interviewdocs.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.VideoNotFoundException;
import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.S3Service;

@RestController
public class VideoController {
    private final VideoRepository repository;

    @Autowired
    S3Service s3Service;
    
    VideoController(VideoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/videos")
    List<Video> all() {
        return repository.findAll();
        // Modify list and add S3 signed URL to each object
    }

    @PostMapping("/videos")
    Video newVideo(@RequestBody Video newVideo) {
        return repository.save(newVideo);
    }

    @GetMapping("/videos/{id}")
    Video one(@PathVariable("id") Long id) {
        return repository.findById(id)
        .orElseThrow(() -> new VideoNotFoundException(id));
    }

    @PutMapping("/videos/{id}")
    Video replaceQuestion(@RequestBody Video newVideo, @PathVariable("id") Long id) {
        
        return repository.findById(id)
        .map(video -> {
            video.setVideoTitle(newVideo.getVideoTitle());
            return repository.save(video);
        })
        .orElseGet(() -> {
            return repository.save(newVideo);
        });
    }

    @DeleteMapping("/videos/{id}")
    void deleteQuestion(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
    
    
}
