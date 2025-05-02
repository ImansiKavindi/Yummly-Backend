package com.yummly.web.dto;

public class CommentDTO {

    private Long id;
    private String content;
    private String username;
    private Long userId;

    public CommentDTO() {}

    public CommentDTO(Long id, String content, String username, Long userId) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
