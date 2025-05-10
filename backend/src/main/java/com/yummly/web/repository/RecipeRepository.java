package com.yummly.web.repository;

import com.yummly.web.model.Recipe;
import com.yummly.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUser(User user);
    
    List<Recipe> findByCuisineContainingIgnoreCase(String cuisine);
    
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> findByIngredientContaining(@Param("ingredient") String ingredient);
    
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.dietaryPreferences d WHERE LOWER(d) LIKE LOWER(CONCAT('%', :preference, '%'))")
    List<Recipe> findByDietaryPreferenceContaining(@Param("preference") String preference);
    
    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Recipe> searchRecipes(@Param("query") String query);
    
    // Find latest recipes (for homepage)
    List<Recipe> findTop10ByOrderByCreatedAtDesc();
} 