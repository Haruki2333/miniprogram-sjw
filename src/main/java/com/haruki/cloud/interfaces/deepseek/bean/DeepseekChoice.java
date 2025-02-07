package com.haruki.cloud.interfaces.deepseek.bean;

public class DeepseekChoice {
    private DeepseekMessage message;
    private int index;
    private String finish_reason;

    // Getters and Setters
    public DeepseekMessage getMessage() {
        return message;
    }

    public void setMessage(DeepseekMessage message) {
        this.message = message;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
}
