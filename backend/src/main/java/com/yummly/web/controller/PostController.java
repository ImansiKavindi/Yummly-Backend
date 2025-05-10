package com.yummly.web.controller;

import com.yummly.web.dto.PostDTO;
import com.yummly.web.model.Post;
import com.yummly.web.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) {
        Post createdPost = postService.createPost(title, description, userId, image, video);

        if (createdPost == null) {
            return ResponseEntity.status(500).body(null);  
        }

        return ResponseEntity.ok(createdPost); 
    }

    @GetMapping("/")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            List<PostDTO> postDTOs = posts.stream().map(PostDTO::new).toList();
            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }





    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(new PostDTO(post));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) {
        Post postToUpdate = postService.getPostById(id);

        if (postToUpdate == null) {
            return ResponseEntity.status(404).body(null);  // Return 404 if the post doesn't exist
        }

        // Updating the post's details
        postToUpdate.setTitle(title);
        postToUpdate.setDescription(description);

        // Handle file updates (optional)
        if (image != null && !image.isEmpty()) {
            String imagePath = postService.saveMediaFile(image);  
            postToUpdate.setImagePath(imagePath); // ✅ 
        }

        if (video != null && !video.isEmpty()) {
            String videoPath = postService.saveMediaFile(video);
            postToUpdate.setVideoPath(videoPath);
        }

       
        Post updatedPost = postService.updatePost(postToUpdate);

        return ResponseEntity.ok(updatedPost);  
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        List<PostDTO> postDTOs = posts.stream().map(PostDTO::new).toList();
        return ResponseEntity.ok(postDTOs);
    }


    
}
