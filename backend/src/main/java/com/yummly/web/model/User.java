package com.yummly.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class User {
    @Id
    private long id;
    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Post> posts;

    public User() {}

  
    public User(long id, String name, String email, String password, List<Post> posts) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.posts = posts;
    }

   
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getter and Setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter methods for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter methods for posts
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
