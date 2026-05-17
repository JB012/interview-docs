package com.interviewdocs.server.model;
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
        this.question = question;
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

    @Override
    public String toString() {
        return "Question: " + this.question + " User ID: " + this.userId;
    }
}