package com.interviewdocs.server.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.interviewdocs.model.Video;
import com.interviewdocs.repository.VideoRepository;
import com.interviewdocs.services.S3Service;
import com.interviewdocs.services.VideoService;
import com.interviewdocs.utils.PagedResponse;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(environments = "tests")
public class VideoServiceUnitTest {
    @Inject
    VideoRepository videoRepository;

    @Inject
    VideoService videoService;

    @Inject 
    S3Service s3Service;

    final static String USER_ID = "1234";
    final static Long QUESTION_ID = 9L;

    final List<Video> videos = List.of(
        new Video("video1"),
        new Video("video2"),
        new Video("video3")
    );

    final int page = 0;
    final int size = videos.size();
    final String field = "title";


    @Test
    void testgetVideosPagedResponse() throws Exception {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Video> videoPage = Page.of(videos, pageable, Long.valueOf(size));

        when(s3Service.createSignedUrl(any(), any(), any())).thenReturn("signed-url");
        when(videoRepository.findAllByUserIdAndQuestionId(USER_ID, QUESTION_ID, pageable)).thenReturn(videoPage);
        
        PagedResponse<Video> pagedResponseResult = videoService.getVideos(page, size, "title,asc", USER_ID, QUESTION_ID);
        
        assertEquals(videos, pagedResponseResult.getContent());
        assertEquals(size, pagedResponseResult.getTotalSize());
        assertEquals(size, pagedResponseResult.getPageSize());
        assertEquals(page, pagedResponseResult.getPageNumber());
    }

    @Test
    void testgetQuestionsAscPageable() throws Exception {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        Page<Video> videoPage = Page.of(videos, pageable, Long.valueOf(size));

        when(s3Service.createSignedUrl(any(), any(), any())).thenReturn("signed-url");
        when(videoRepository.findAllByUserIdAndQuestionId(USER_ID, QUESTION_ID, pageable)).thenReturn(videoPage);

        videoService.getVideos(page, size, "title,asc", USER_ID, QUESTION_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(videoRepository).findAllByUserIdAndQuestionId(eq(USER_ID), eq(QUESTION_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(order.isAscending());
    }

    @Test
    void testQuestionsDescPageable() throws Exception {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        Page<Video> videoPage = Page.of(videos, pageable, Long.valueOf(size));

        when(s3Service.createSignedUrl(any(), any(), any())).thenReturn("signed-url");
        when(videoRepository.findAllByUserIdAndQuestionId(USER_ID, QUESTION_ID, pageable)).thenReturn(videoPage);

        videoService.getVideos(page, size, "title,desc", USER_ID, QUESTION_ID);
    
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        
        verify(videoRepository).findAllByUserIdAndQuestionId(eq(USER_ID), eq(QUESTION_ID), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getNumber());
        assertEquals(size, capturedPageable.getSize());

        Sort.Order order = capturedPageable.getSort().getOrderBy().get(0);

        assertEquals(field, order.getProperty());
        assertTrue(!order.isAscending());
    }

    @MockBean(VideoRepository.class)
    VideoRepository videoRepository() {
        return mock(VideoRepository.class);
    }

    @MockBean(S3Service.class)
    S3Service s3Service() {
        return mock(S3Service.class);
    }
}
