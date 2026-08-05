package com.interviewdocs.utils;

import jakarta.inject.Singleton;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Singleton
public class CloudFrontPrivateKeyProvider {

    private final String privateKey;

    public CloudFrontPrivateKeyProvider() {

        try (SsmClient ssm = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            GetParameterRequest request = GetParameterRequest.builder()
                    .name("/interviewdocs/PRIVATE_KEY")
                    .withDecryption(true)
                    .build();

            this.privateKey = ssm.getParameter(request)
                    .parameter()
                    .value();

        }
    }

    public String getPrivateKey() {
        return privateKey;
    }
}