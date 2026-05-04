package com.interviewdocs.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interviewdocs.server.error.VideoNotFoundException;
import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.*;
import com.interviewdocs.server.services.VideoService;

@RestController
public class VideoController {
    private final VideoRepository repository;
    
    @Autowired
    private VideoService videoService;

    VideoController(VideoRepository repository) {
        this.repository = repository;
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
            video.setTitle(newVideo.getTitle());
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
