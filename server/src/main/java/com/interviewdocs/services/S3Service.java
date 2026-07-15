package com.interviewdocs.server.services;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;
import software.amazon.awssdk.services.cloudfront.model.Paths;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.cloudfront.CloudFrontAsyncClient;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;

import com.interviewdocs.server.utils.CreateCannedPolicyRequest;

import jakarta.inject.Singleton;

@Singleton
public class S3Service {
    private static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    private static final CloudFrontAsyncClient cloudFrontClient = CloudFrontAsyncClient.create();
    private static final S3AsyncClient s3Client = S3AsyncClient.create();

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

    public void deleteS3Object(String bucketName, String keyName) {
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(keyName)
            .build();

        CompletableFuture<DeleteObjectResponse> response = s3Client.deleteObject(deleteReq);
        
        response.whenComplete((deleteRes, ex) -> {
            if (deleteRes != null) {
                System.out.println("Original file " + keyName + "has been deleted");
                
                String distributionId = "E83CXEY1RSF2N";
                
                Paths paths = Paths.builder()
                    .quantity(1)
                    .items("/" + keyName)
                    .build();

                InvalidationBatch invalidationBatch = InvalidationBatch.builder()
                    .callerReference(keyName)
                    .paths(paths)
                    .build();

                CreateInvalidationRequest invalidationRequest = CreateInvalidationRequest.builder()
                    .distributionId(distributionId)
                    .invalidationBatch(invalidationBatch)
                    .build();

                CompletableFuture<CreateInvalidationResponse> invalidationResponse = cloudFrontClient.createInvalidation(invalidationRequest);

                invalidationResponse.whenComplete((invalidateRes, invalidateEx) -> {
                    if (invalidateRes != null) {
                        System.out.println("Invalidated cloudfront cache for file " + keyName);
                    }
                    else {    
                        throw new RuntimeException("An CloudFront exception occurred during invalidation", ex);
                    }
                });
            }
            else {
                throw new RuntimeException("An S3 exception occurred during delete", ex);
            }
        });
    }
    public void changeObjectName(String bucketName, String keyName, String newKeyName) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
            .sourceBucket(bucketName)
            .sourceKey(keyName)
            .destinationBucket(bucketName)
            .destinationKey(newKeyName)
            .build();

        CompletableFuture<CopyObjectResponse> response = s3Client.copyObject(copyReq);
        response.whenComplete((copyRes, ex) -> {
            if (copyRes != null) {
                System.out.println("The object with the name " + newKeyName + " was copied to " + bucketName);
                deleteS3Object(bucketName, keyName);
            } else {
                System.out.println("ex is " + ex.toString());
                throw new RuntimeException("An S3 exception occurred during copy", ex);
            }
        });
    }
}
