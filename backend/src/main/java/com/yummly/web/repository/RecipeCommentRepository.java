package com.yummly.web.repository;

import com.yummly.web.model.Recipe;
import com.yummly.web.model.RecipeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
    List<RecipeComment> findByRecipeOrderByCreatedAtDesc(Recipe recipe);
} 