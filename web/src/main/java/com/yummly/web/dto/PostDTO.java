package com.yummly.web.dto;

import com.yummly.web.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String imagePath;
    private String videoPath;
    private String description;
    private String userName;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.imagePath = post.getImagePath();
        this.videoPath = post.getVideoPath();
        this.description = post.getDescription();
        this.userName = post.getUser() != null ? post.getUser().getName() : null;
    }
}
