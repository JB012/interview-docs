package com.interviewdocs.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;

import com.interviewdocs.model.*;
import com.interviewdocs.services.VideoService;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.Principal;

import com.interviewdocs.utils.PagedResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/videos")
public class VideoController {
    private final VideoService videoService;
    

    VideoController(VideoService videoService) {
        this.videoService = videoService;
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
        return videoService.uploadVideo(videoBuffer);
    }

    @Post
    HttpResponse<Video> newVideo(@Body Video newVideo, @QueryValue(value="questionId") Long questionId) { 
        return HttpResponse.ok(videoService.postVideo(newVideo, questionId));
    }

    @Get("/{id}")
    HttpResponse<Video> one(@PathVariable("id") Long id) {
        return HttpResponse.ok(videoService.getVideo(id));
    }

    @Put("/{id}")
    void replaceVideo(@Body Video newVideo, @PathVariable("id") Long id) {
        videoService.putVideo(id, newVideo);
    }

    @Delete("/{id}")
    void deleteVideo(@PathVariable("id") Long id) {
        videoService.deleteVideo(id);
    } 
}
