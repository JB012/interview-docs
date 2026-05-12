package com.interviewdocs.server.model;
import java.net.URLEncoder;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table (name = "videos")
public class Video {
    private @Id
    @GeneratedValue long id;
    
    @JsonProperty("source") 
    @Column(columnDefinition = "TEXT")
    private String source;
    
    @JsonProperty("title") 
    @Column(columnDefinition = "TEXT")
    private String title;
    
    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;

    @JsonProperty("question_id")
    @Column(columnDefinition = "INT")
    private long questionId;
    
    Video() {}

    Video(String title) {
        this.title = title;
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

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getQuestionId() {
        return this.questionId;
    }

    public String getKeyName() {
        return userId + "/" + title;
    }

    @Override
    public String toString() {
        return "Video is named " + this.title;
    }
}