package com.interviewdocs.server.model;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import io.micronaut.serde.annotation.Serdeable;

import java.util.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Serdeable
@Entity
@Table (name = "videos")
public class Video {
    @Id @JsonProperty("video_id") 
    @GeneratedValue @Column(name="video_id") 
    public Long id;
    
    @JsonProperty("source") 
    @Column(columnDefinition = "TEXT")
    private String source;
    
    @JsonProperty("title") 
    @Column(columnDefinition = "TEXT")
    private String title;
    
    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;
    
    @JsonProperty("created_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="question_id", nullable = false)
    private Question question;

    public Video() {}

    Video(String title) {
        this.title = title;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public String getKeyName() {
        return userId + "/" + title;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
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

    public void addQuestion(Question question) {
        this.question = question;
    }

    public void removeQuestion() {
        this.question = null;
    }

    @JsonIgnore
    public Question getQuestion() {
        return this.question;
    }

    public void setTime(Video video) {
        this.setEditedAt(video.getEditedAt() == null ? this.editedAt : video.getEditedAt());
        this.setViewedAt(video.getViewedAt() == null ? this.viewedAt : video.getViewedAt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return video.getId().equals(getId());
    }

    @Override
    public String toString() {
        return "Video is named " + this.title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}