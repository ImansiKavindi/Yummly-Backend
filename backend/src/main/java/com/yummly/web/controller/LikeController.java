package com.yummly.web.controller;

import com.yummly.web.model.Like;
import com.yummly.web.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/like/{postId}/{userId}")
    public ResponseEntity<Like> addLike(@PathVariable Long postId, @PathVariable Long userId) {
        Like like = likeService.addLike(postId, userId);
        if (like != null) {
            return ResponseEntity.ok(like);
        }
        return ResponseEntity.status(400).body(null);  // Already liked
    }

    @DeleteMapping("/like/{postId}/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.removeLike(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPostId(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPostId(postId);
        return ResponseEntity.ok(likes);
    }
}
 