package com.interviewdocs.server.error;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(Long id) {
        super("Could not find folder " + id);
    }
}
