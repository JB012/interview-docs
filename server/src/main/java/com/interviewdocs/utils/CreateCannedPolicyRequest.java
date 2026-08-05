package com.interviewdocs.utils;

import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import io.micronaut.context.env.Environment;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CreateCannedPolicyRequest {
    @Inject
    Environment environment;
    
    private static PrivateKey loadPrivateKeyFromJar() throws Exception {
        try (InputStream is = CreateCannedPolicyRequest.class.getClassLoader()
                .getResourceAsStream("private_key_pkcs8.der")) {
            
            if (is == null) {
                throw new IllegalArgumentException("Key file not found in JAR");
            }
            
            byte[] keyBytes = is.readAllBytes();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    public CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName,
        String fileName, String privateKeyFullPath, String publicKeyId) throws Exception {
            
        String protocol = "https";
        String resourcePath = "/" + fileName;

        String cloudFrontUrl = new URI(protocol + "://" + distributionDomainName + resourcePath).toURL().toString();
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        Path path = Paths.get(privateKeyFullPath);


        if (environment.getActiveNames().contains("prod")) {
            return  CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(loadPrivateKeyFromJar())
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
        }
        return CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(path)
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
    }
}