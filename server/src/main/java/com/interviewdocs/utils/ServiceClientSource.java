package com.interviewdocs.server.utils;

import software.amazon.awssdk.services.s3.S3AsyncClient;

public class ServiceClientSource {
    private static final S3AsyncClient s3Client = S3AsyncClient.create();
    
    public static S3AsyncClient getS3Client() {
        return s3Client;
    }
}