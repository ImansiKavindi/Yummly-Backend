package com.yummly.web.controller;

import com.yummly.web.dto.RecipeCommentDTO;
import com.yummly.web.dto.RecipeDTO;
import com.yummly.web.model.Recipe;
import com.yummly.web.model.RecipeComment;
import com.yummly.web.model.User;
import com.yummly.web.repository.RecipeCommentRepository;
import com.yummly.web.repository.RecipeRepository;
import com.yummly.web.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private RecipeCommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    // Get all recipes
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeDTO> recipeDTOs = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOs);
    }
    
    // Get recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(recipeOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }
    
    // Create new recipe
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO recipeDTO, @RequestHeader("userid") Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Recipe recipe = convertToEntity(recipeDTO);
        recipe.setUser(userOpt.get());
        Recipe savedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedRecipe));
    }
    
    // Update recipe
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO, 
                                                @RequestHeader("userid") Long userId) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Recipe recipe = recipeOpt.get();
        // Check if user is the owner
        if (recipe.getUser().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Update recipe fields
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setImageUrl(recipeDTO.getImageUrl());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setCuisine(recipeDTO.getCuisine());
        recipe.setDietaryPreferences(recipeDTO.getDietaryPreferences());
        recipe.setPrepTimeMinutes(recipeDTO.getPrepTimeMinutes());
        recipe.setCookTimeMinutes(recipeDTO.getCookTimeMinutes());
        recipe.setServings(recipeDTO.getServings());
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(convertToDTO(updatedRecipe));
    }
    
    // Delete recipe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id, @RequestHeader("userid") Long userId) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Recipe recipe = recipeOpt.get();
        // Check if user is the owner
        if (recipe.getUser().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        recipeRepository.delete(recipe);
        return ResponseEntity.noContent().build();
    }
    
    // Get recipes by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Recipe> recipes = recipeRepository.findByUser(userOpt.get());
        List<RecipeDTO> recipeDTOs = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOs);
    }
    
    // Search recipes
    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(@RequestParam(required = false) String query,
                                                      @RequestParam(required = false) String ingredient,
                                                      @RequestParam(required = false) String cuisine,
                                                      @RequestParam(required = false) String dietaryPreference) {
        List<Recipe> recipes;
        
        if (query != null && !query.isEmpty()) {
            recipes = recipeRepository.searchRecipes(query);
        } else if (ingredient != null && !ingredient.isEmpty()) {
            recipes = recipeRepository.findByIngredientContaining(ingredient);
        } else if (cuisine != null && !cuisine.isEmpty()) {
            recipes = recipeRepository.findByCuisineContainingIgnoreCase(cuisine);
        } else if (dietaryPreference != null && !dietaryPreference.isEmpty()) {
            recipes = recipeRepository.findByDietaryPreferenceContaining(dietaryPreference);
        } else {
            recipes = recipeRepository.findAll();
        }
        
        List<RecipeDTO> recipeDTOs = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOs);
    }
    
    // Get latest recipes
    @GetMapping("/latest")
    public ResponseEntity<List<RecipeDTO>> getLatestRecipes() {
        List<Recipe> recipes = recipeRepository.findTop10ByOrderByCreatedAtDesc();
        List<RecipeDTO> recipeDTOs = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOs);
    }
    
    // Add comment to recipe
    @PostMapping("/{id}/comments")
    public ResponseEntity<RecipeCommentDTO> addComment(@PathVariable Long id, @RequestBody RecipeCommentDTO commentDTO,
                                                    @RequestHeader("userid") Long userId) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (recipeOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        RecipeComment comment = new RecipeComment();
        comment.setContent(commentDTO.getContent());
        comment.setUser(userOpt.get());
        comment.setRecipe(recipeOpt.get());
        
        RecipeComment savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToCommentDTO(savedComment));
    }
    
    // Get comments for recipe
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<RecipeCommentDTO>> getComments(@PathVariable Long id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<RecipeComment> comments = commentRepository.findByRecipeOrderByCreatedAtDesc(recipeOpt.get());
        List<RecipeCommentDTO> commentDTOs = comments.stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }
    
    // Helper methods to convert between entities and DTOs
    private RecipeDTO convertToDTO(Recipe recipe) {
        RecipeDTO dto = modelMapper.map(recipe, RecipeDTO.class);
        dto.setUserId(recipe.getUser().getId());
        dto.setUserName(recipe.getUser().getName());
        
        // Add comments
        List<RecipeCommentDTO> commentDTOs = recipe.getComments().stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
        dto.setComments(commentDTOs);
        
        return dto;
    }
    
    private Recipe convertToEntity(RecipeDTO dto) {
        return modelMapper.map(dto, Recipe.class);
    }
    
    private RecipeCommentDTO convertToCommentDTO(RecipeComment comment) {
        RecipeCommentDTO dto = modelMapper.map(comment, RecipeCommentDTO.class);
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setRecipeId(comment.getRecipe().getId());
        return dto;
    }
} 