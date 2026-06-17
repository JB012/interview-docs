package com.interviewdocs.server.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table (name = "folders")
@JsonIgnoreProperties({"questions"})
public class Folder {
    @Id @JsonProperty("folder_id")
    @GeneratedValue Long folderId;

    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;
    
    @JsonProperty("title")
    @Column(columnDefinition = "TEXT")
    private String title;

    @JsonProperty("created_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;
    
    @ManyToMany(mappedBy = "folders")
    private Set<Question> questions = new HashSet<>();

    Folder() {}

    Folder(String title) {
        this.title = title;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(long id) {
        this.folderId = id;
    }

    public long getId() {
        return folderId;
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setTime(Folder folder) {
        this.setEditedAt(folder.getEditedAt() == null ? this.editedAt : folder.getEditedAt());
        this.setViewedAt(folder.getViewedAt() == null ? this.viewedAt : folder.getViewedAt());
    }

    public OffsetDateTime getViewedAt() {
        return this.viewedAt;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.getFolders().add(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.getFolders().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return folder.getId() == getId();
    }
}
