package com.interviewdocs.server.restservice;

public class Questions {
    private Long id;
    private String question;
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getQuestion() {
        return question;
    }
}