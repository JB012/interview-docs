package com.interviewdocs.utils;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.inject.Singleton;

@Singleton
public class CloudFrontKeyFile {

    private final Path path;

    public CloudFrontKeyFile(CloudFrontPrivateKeyProvider provider,
            Environment environment,
            @Value("${PRIVATE_KEY_NAME}") String privateKeyName) throws IOException {
        if (environment.getActiveNames().contains(Environment.DEVELOPMENT)) {
            path = Path.of(System.getProperty("java.io.tmpdir"))
                       .resolve(privateKeyName);
        } else {
            path = Path.of("/tmp/" + privateKeyName);
        }

        if (!Files.exists(path)) {
            Files.writeString(path, provider.getPrivateKey());
        }
    }

    public Path getPath() {
        return path;
    }
}