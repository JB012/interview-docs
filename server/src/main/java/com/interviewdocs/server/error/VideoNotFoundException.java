package com.interviewdocs.server.error;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(Long id) {
        super("Could not find video " + id);
    }
}
