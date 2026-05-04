package com.interviewdocs.server.services;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;

import com.interviewdocs.server.utils.CreateCannedPolicyRequest;

@Service
public class S3Service {
    private static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    
    public String createPresignedGetUrl(String bucketName, String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toExternalForm();
        }
    }

    
    public String createSignedUrl(String keyName, String keyPairId,
                                  String privateKeyPath, Instant expiration,
                                  String hashAlgorithm) throws Exception {
        String distributionDomainName = "d2b2nwmiecq4jm.cloudfront.net";
        CannedSignerRequest request = CreateCannedPolicyRequest.createRequestForCannedPolicy(
            distributionDomainName, 
            keyName, 
            privateKeyPath, 
            keyPairId
        );

        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);
        
        return signedUrl.url();
    }
}
