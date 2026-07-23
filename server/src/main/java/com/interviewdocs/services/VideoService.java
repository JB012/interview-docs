package com.interviewdocs.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import com.interviewdocs.utils.PagedResponse;
import io.micronaut.data.model.Sort;

import com.interviewdocs.model.Video;
import com.interviewdocs.repository.VideoRepository;

import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public class VideoService {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    public Object setSourceToPresignedURL;
    
    public VideoService(S3Service s3Service, VideoRepository videoRepository) {
        this.s3Service = s3Service;
        this.videoRepository = videoRepository;
    }

    private static String DEVELOPMENT_PRIVATE_KEY_PATH = "C:\\Users\\jamal\\OneDrive\\Documents\\web-projects\\interview-docs\\server\\private_key_pkcs8.der";

    public void setSourceToPresignedURL(Video video) throws Exception {
        String keyPairId = "K2YICAIZIAAWQH";
        Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);

        String signedURL = s3Service.createSignedUrl(video.getKeyName(), keyPairId, DEVELOPMENT_PRIVATE_KEY_PATH, expiration, "SHA256");
        video.setSource(signedURL);
    }

    public String getUserIdNumber(String userId) {
        // Auth0 User ID : {connection}|{userIdNumber}. 
        // Connection is irrelevant and the vertical bar character isn't recommended for S3 naming convention
        return userId.split("\\|")[1];
    }

    public PagedResponse<Video> getVideos(int page, int size, String sort, String userId, Long questionId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.trim().toLowerCase().equals("desc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        }
        else if (direction.trim().toLowerCase().equals("asc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        }
        
        Page<Video> videoPage = videoRepository.findAllByUserIdAndQuestionId(userId, questionId, pageable);

        videoPage.map((video) -> {
            try { 
                setSourceToPresignedURL(video);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            return video;
        });

        return new PagedResponse<Video>(
            videoPage.getContent(),
            videoPage.getTotalSize(),
            videoPage.getContent().size(),
            pageable.getNumber()
        );
    }
}
