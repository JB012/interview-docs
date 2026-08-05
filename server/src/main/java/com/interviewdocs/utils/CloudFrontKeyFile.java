package com.interviewdocs.utils;
import io.micronaut.context.env.Environment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.inject.Singleton;

@Singleton
public class CloudFrontKeyFile {

    private final Path path;

    public CloudFrontKeyFile(CloudFrontPrivateKeyProvider provider,
            Environment environment) throws IOException {

        final String PRIVATE_KEY_NAME = System.getenv("PRIVATE_KEY_NAME");

        if (environment.getActiveNames().contains(Environment.DEVELOPMENT)) {
            path = Path.of(System.getProperty("java.io.tmpdir"))
                       .resolve(PRIVATE_KEY_NAME);
        } else {
            path = Path.of("/tmp/" + PRIVATE_KEY_NAME);
        }

        if (!Files.exists(path)) {
            Files.writeString(path, provider.getPrivateKey());
        }
    }

    public Path getPath() {
        return path;
    }
}