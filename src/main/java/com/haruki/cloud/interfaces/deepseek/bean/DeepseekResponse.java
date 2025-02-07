package com.haruki.cloud.interfaces.deepseek.bean;


public class DeepseekResponse {
    private String id;
    private DeepseekChoice[] choices;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeepseekChoice[] getChoices() {
        return choices;
    }

    public void setChoices(DeepseekChoice[] choices) {
        this.choices = choices;
    }
}
