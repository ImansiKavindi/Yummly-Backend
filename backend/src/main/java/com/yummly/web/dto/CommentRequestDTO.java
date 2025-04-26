package com.yummly.web.dto;

public class CommentRequestDTO {

    private String content;

    public CommentRequestDTO() {
    }

    public CommentRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
