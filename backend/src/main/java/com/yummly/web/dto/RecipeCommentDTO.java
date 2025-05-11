package com.yummly.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommentDTO {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long recipeId;
    private Long createdAt;
} 