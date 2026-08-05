package com.interviewdocs.utils;

import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CreateCannedPolicyRequest {
    public CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName,
        String fileName, String privateKeyFullPath, String publicKeyId) throws Exception {
            
        String protocol = "https";
        String resourcePath = "/" + fileName;

        String cloudFrontUrl = new URI(protocol + "://" + distributionDomainName + resourcePath).toURL().toString();
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        Path path = Paths.get(privateKeyFullPath);

        return CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(path)
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
    }
}