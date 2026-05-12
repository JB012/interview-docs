package com.interviewdocs.server.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table (name = "questions")
public class Question {
    private @Id
    @GeneratedValue Long id;
    
    private String question;

    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("user_text")
    private String userText;
    
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

    public void setUserText(String userText) {
        this.userText = userText;
    }
    
    public String getUserText() {
        return this.userText;
    }

    @Override
    public String toString() {
        return "Question: " + this.question + " User ID: " + this.userId;
    }
}