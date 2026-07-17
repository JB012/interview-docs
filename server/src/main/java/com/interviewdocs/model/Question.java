package com.interviewdocs.server.model;
import java.time.OffsetDateTime;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.*;
import io.micronaut.serde.annotation.Serdeable;

import org.hibernate.annotations.BatchSize;

@Serdeable
@Entity
@Table(name = "questions")
@JsonIgnoreProperties({"folders", "videos"})
public class Question {
    private @Id @JsonProperty("question_id") @Column(name="question_id")
    @GeneratedValue Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @JsonProperty("user_id")
    @Column(columnDefinition = "TEXT")
    private String userId;
    
    @JsonProperty("answer")
    @Column(columnDefinition = "TEXT")
    private String answer;
    
    @JsonProperty("created_at")
    @Column(name="created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(name="edited_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(name="viewed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;
    
    @ManyToMany
    @BatchSize(size = 20)
    @JoinTable(name = "question_folder",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private Set<Folder> folders = new HashSet<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Video> videos = new HashSet<>();

    public Question() {}

    Question(String question) {
        this.question = question;
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

    public void setQuestion(String question) {
        if (question.equals("")) {
            this.question = "What is your question?";
        }
        else {
            this.question = question;
        }
    }
    
    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getAnswer() {
        return this.answer;
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

    public void setEverything(Question question) {
        this.setAnswer(question.getAnswer() == null ? this.answer : question.getAnswer());
        this.setQuestion(question.getQuestion() == null ? this.question : question.getQuestion());
        this.setEditedAt(question.getEditedAt() == null ? this.editedAt : question.getEditedAt());
        this.setViewedAt(question.getViewedAt() == null ? this.viewedAt : question.getViewedAt());
    }

    public void addToFolder(Folder folder) {
        folders.add(folder);
        folder.getQuestions().add(this);
    }

    public void removeFromFolder(Folder folder) {
        folders.remove(folder);
        folder.getQuestions().remove(this);
    }

    public void addVideo(Video video) {
        videos.add(video);
        video.addQuestion(this);
    }
    
    public void removeVideo(Video video) {
        videos.remove(video);
        video.removeQuestion();
    }

    public Set<Video> getVideos() {
        return this.videos;
    }

    public void removeAllVideos() {
        videos.clear();
    }

    public Set<Folder> getFolders() {
        return this.folders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return question.getId().equals(getId());
    }

    @Override 
    public String toString() {
        return "Question: " + this.question + " User ID: " + this.userId + " ID: " + this.id 
        + " Created At: " + this.createdAt + " Viewed At: " + this.viewedAt + " Edited At: " + this.editedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}