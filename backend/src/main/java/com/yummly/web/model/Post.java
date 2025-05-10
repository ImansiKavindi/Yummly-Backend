package com.yummly.web.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Entity
@Table(name = "posts")
public class Post {

    // Getters and Setters
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
    

    // Constructors
    public Post() {}

    // Method to get the username from the associated User object
    public String getUserName() {
        return user != null ? user.getName() : "Unknown User";  // Returns the user's name or "Unknown User" if null
    }

    public void setUserId(Long userId) {
        User user = new User();
        user.setId(userId);
        this.user = user;
    }

    public void setVideo(MultipartFile video) {
    }

    public void setImage(MultipartFile image) {
    }
}

