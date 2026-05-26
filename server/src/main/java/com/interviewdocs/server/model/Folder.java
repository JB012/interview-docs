package com.interviewdocs.server.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "folders")
public class Folder {
    @Id @JsonProperty("folder_id")
    @GeneratedValue Long id;

    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;
    
    @Column(columnDefinition = "TEXT")
    private String name;

    @JsonProperty("created_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;
    
    Folder() {}

    Folder(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setEditedAt(OffsetDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public OffsetDateTime getEditedAt() {
        return this.editedAt;
    }

    public void setViewedAt(OffsetDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public OffsetDateTime getViewedAt() {
        return this.viewedAt;
    }
}
