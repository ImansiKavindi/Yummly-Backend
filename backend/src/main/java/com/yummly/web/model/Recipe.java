package com.yummly.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(length = 2000)
    private String instructions;
    
    private String imageUrl;
    
    @ElementCollection
    private List<String> ingredients = new ArrayList<>();
    
    private String cuisine;
    
    @ElementCollection
    private List<String> dietaryPreferences = new ArrayList<>();
    
    private Integer prepTimeMinutes;
    
    private Integer cookTimeMinutes;
    
    private Integer servings;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RecipeComment> comments = new ArrayList<>();
    
    // Added createdAt for sorting and displaying
    private Long createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
} 