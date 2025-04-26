package com.yummly.web.controller;

import com.yummly.web.model.Like;
import com.yummly.web.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    // Toggle like/unlike
    @PostMapping
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        Like like = likeService.toggleLike(postId, userId);
        if (like != null) {
            return ResponseEntity.ok("Liked");
        } else {
            return ResponseEntity.ok("Unliked");
        }
    }

    // Check if user liked this post
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserLiked(@PathVariable Long postId, @RequestParam Long userId) {
        return ResponseEntity.ok(likeService.hasUserLiked(postId, userId));
    }

    // Get total like count
    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikeCount(postId));
    }

    // View all likes for a post
    @GetMapping
    public ResponseEntity<List<Like>> getLikesByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesByPostId(postId));
    }
}
