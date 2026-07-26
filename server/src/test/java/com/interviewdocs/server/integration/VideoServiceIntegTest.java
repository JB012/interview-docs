package com.interviewdocs.server.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
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
        new Video(USER_ID, "Avideo", OffsetDateTime.parse("2026-07-24T07:52:00Z"), 
        OffsetDateTime.parse("2026-07-24T06:35:00Z")),
        new Video(USER_ID, "Bvideo", OffsetDateTime.parse("2026-07-22T09:26:00Z"), 
        OffsetDateTime.parse("2026-07-22T08:12:00Z")),
        new Video(USER_ID, "Cvideo", OffsetDateTime.parse("2026-07-25T08:52:00Z"), 
        OffsetDateTime.parse("2026-07-25T09:35:00Z"))
    );

    final Question question = new Question(USER_ID, "question");

    final int page = 0, size = videos.size();
    
    void testgetVideosSortHelper(String sort, List<String> expected) {
        PagedResponse<Video> videoPage = videoService.getVideos(page, size, sort, USER_ID, question.getId());
        
        List<String> result = videoPage.getContent().stream().map(video -> video.getTitle()).toList();

        assertEquals(expected, result);
        assertEquals(page, videoPage.getPageNumber());
        assertEquals(size, videoPage.getTotalSize());
        assertEquals(size, videoPage.getPageSize());
    }
    
    @Test
    void testgetVideosSort() {
        questionRepository.insert(question);

        for (Video v : videos) {
            question.addVideo(v);
            videoRepository.save(v);
        }

        questionRepository.save(question);

        testgetVideosSortHelper("title, asc", List.of("Avideo", "Bvideo", "Cvideo"));
        testgetVideosSortHelper("title, desc", List.of("Cvideo", "Bvideo", "Avideo"));

        testgetVideosSortHelper("editedAt, asc", List.of("Bvideo", "Avideo", "Cvideo"));
        testgetVideosSortHelper("editedAt, desc", List.of("Cvideo", "Avideo", "Bvideo"));

        testgetVideosSortHelper("viewedAt, asc", List.of("Bvideo", "Avideo", "Cvideo"));
        testgetVideosSortHelper("viewedAt, desc", List.of("Cvideo", "Avideo", "Bvideo"));
    }
}
