package com.interviewdocs.server.model;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table (name = "questions")
public class Question {
    private @Id
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
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("edited_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime editedAt;

    @JsonProperty("viewed_at")
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime viewedAt;
    
    Question() {}

    Question(String question) {
        this.question = question;
    }

    public void setUser_id(String userId) {
        this.userId = userId;
    }

    public String getUser_id() {
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

    @Override
    public String toString() {
        return "Question: " + this.question + " User ID: " + this.userId + " ID: " + this.id 
        + " Created At: " + this.createdAt + " Viewed At: " + this.viewedAt + " Edited At: " + this.editedAt;
    }
}