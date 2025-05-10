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

   
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        boolean isLiked = likeService.toggleLike(postId, userId);
        return ResponseEntity.ok(isLiked ? "Liked" : "Unliked");
    }

    
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserLiked(@PathVariable Long postId, @RequestParam Long userId) {
        boolean hasLiked = likeService.hasUserLiked(postId, userId);
        return ResponseEntity.ok(hasLiked);
    }

   
    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping
    public ResponseEntity<List<Like>> getLikesByPost(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPostId(postId);
        return ResponseEntity.ok(likes);
    }
}
 