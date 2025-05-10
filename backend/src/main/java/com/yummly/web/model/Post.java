package com.yummly.web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "image")
    private String imagePath;

    @Column(name = "video")
    private String videoPath;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile videoFile;

    public Post() {}

    public String getUserName() {
        return user != null ? user.getName() : "Unknown User";
    }

    public void setUserId(Long userId) {
        User user = new User();
        user.setId(userId);
        this.user = user;
    }
}
