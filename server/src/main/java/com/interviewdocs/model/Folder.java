package com.interviewdocs.server.model;

import java.time.OffsetDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@Entity
@Table(name = "folders")
@JsonIgnoreProperties({"questions"})
public class Folder {
    @Id @JsonProperty("folder_id")
    @GeneratedValue @Column(name="folder_id")
    public Long id;

    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;
    
    @JsonProperty("title")
    @Column(columnDefinition = "TEXT")
    private String title;

    @JsonProperty("created_at")
    @Column(name="created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(name="edited_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(name="viewed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;
    
    @ManyToMany(mappedBy = "folders")
    private Set<Question> questions = new HashSet<>();

    public Folder() {}

    Folder(String title) {
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
        this.questions.add(question);
        question.getFolders().add(this);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.getFolders().remove(this);
    }

    public void clearFolder() {
        this.questions.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return folder.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
