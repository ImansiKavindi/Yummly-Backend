package com.yummly.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String instructions;
    private String imageUrl;
    private List<String> ingredients = new ArrayList<>();
    private String cuisine;
    private List<String> dietaryPreferences = new ArrayList<>();
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private Long userId;
    private String userName;
    private Long createdAt;
    private List<RecipeCommentDTO> comments = new ArrayList<>();
} 