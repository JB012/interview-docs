package com.interviewdocs.server.model;
import jakarta.persistence.*;

@Entity
@Table (name = "questions")
public class Question {
    private @Id
    @GeneratedValue Long id;
    private String question;
    
    Question() {}

    Question(String question) {
        this.question = question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getQuestion() {
        return question;
    }

    @Override
    public String toString() {
        return "Question is " + this.question;
    }
}