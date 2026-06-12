package com.interviewdocs.server.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.interviewdocs.server.model.Video;
import com.interviewdocs.server.repository.VideoRepository;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private VideoRepository videoRepository;
    
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

    public Page<Video> getVideos(int page, int size, String sort, String userId) {
        String[] sortOptions = sort.split(",");
        
        String field = sortOptions[0];
        String direction = sortOptions[1];

        Pageable pageable = null;

        if (direction.equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(field).descending());
        }
        else {
            pageable = PageRequest.of(page, size, Sort.by(field).ascending());
        }
        
        List<Video> videos = videoRepository.findAllByUserId(userId);

        for (int i = 0; i < videos.size(); i++) {
            try {
                setSourceToPresignedURL(videos.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int toIndex = (page + 1) * size > videos.size() ? videos.size() : (page + 1) * size;
        
        return new PageImpl<>(videos.subList(page * size, toIndex), pageable, videos.size());
    }
}
