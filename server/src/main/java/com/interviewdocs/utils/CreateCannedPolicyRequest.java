package com.interviewdocs.utils;

import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CreateCannedPolicyRequest {
    private final CloudFrontKeyFile cloudFrontKeyFile;

    public CreateCannedPolicyRequest(CloudFrontKeyFile cloudFrontKeyFile) {
        this.cloudFrontKeyFile = cloudFrontKeyFile;
    }
    
    public CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName,
            String fileName, String publicKeyId) throws Exception {
        String protocol = "https";
        String resourcePath = "/" + fileName;

        String cloudFrontUrl = new URI(protocol + "://" + distributionDomainName + resourcePath).toURL().toString();
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        
        return CannedSignerRequest.builder()
            .resourceUrl(cloudFrontUrl)
            .privateKey(cloudFrontKeyFile.getPath())
            .keyPairId(publicKeyId)
            .expirationDate(expirationDate)
            .build();
        
    }
}