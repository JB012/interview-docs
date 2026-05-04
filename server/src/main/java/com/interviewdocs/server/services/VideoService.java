package com.interviewdocs.server.services;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interviewdocs.server.model.Video;

@Service
public class VideoService {
    @Autowired
    private S3Service s3Service;

    
    private static String DEVELOPMENT_PRIVATE_KEY_PATH = "C:\\Users\\jamal\\OneDrive\\Documents\\web-projects\\interview-docs\\server\\private_key_pkcs8.der";

    public void setSourceToPresignedURL(Video video) throws Exception {
        String keyName = URLEncoder.encode(video.getUserId(), "UTF-8") + "/" + video.getTitle();
        String keyPairId = "K2YICAIZIAAWQH";
        Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);

        String signedURL = s3Service.createSignedUrl(keyName, keyPairId, DEVELOPMENT_PRIVATE_KEY_PATH, expiration, "SHA256");
        video.setSource(signedURL);
       
    }
}
