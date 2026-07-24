package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.interviewdocs.model.Question;
import com.interviewdocs.model.Video;
import com.interviewdocs.repository.QuestionRepository;
import com.interviewdocs.repository.VideoRepository;
import com.interviewdocs.services.VideoService;
import com.interviewdocs.utils.PagedResponse;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "test")
public class VideoServiceIntegTest {
    @Inject
    VideoRepository videoRepository;

    @Inject
    VideoService videoService;

    @Inject
    QuestionRepository questionRepository;

    final static String USER_ID = "1234";

    final List<Video> videos = List.of(
        new Video(USER_ID, "Avideo"),
        new Video(USER_ID, "Bvideo"),
        new Video(USER_ID, "Cvideo")
    );

    final Question question = new Question(USER_ID, "question");

    @Test
    void testgetVideosAscSort() {
        String sort = "title, asc";
        int page = 0, size = videos.size();

        questionRepository.insert(question);

        for (Video v : videos) {
            question.addVideo(v);
            videoRepository.save(v);
        }

        questionRepository.save(question);

        PagedResponse<Video> videoPage = videoService.getVideos(page, size, sort, USER_ID, question.getId());
        
        List<String> expected = List.of("Avideo", "Bvideo", "Cvideo");
        List<String> result = videoPage.getContent().stream().map(video -> video.getTitle()).toList();

        assertEquals(expected, result);
        assertEquals(page, videoPage.getPageNumber());
        assertEquals(size, videoPage.getTotalSize());
        assertEquals(size, videoPage.getPageSize());
    }

    //test that video is removed from question when deleted
}
