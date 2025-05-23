package com.yummly.web.controller;

import com.yummly.web.dto.CommentDTO;
import com.yummly.web.dto.CommentRequestDTO;
import com.yummly.web.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

   
    @PostMapping
    public CommentDTO addComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody CommentRequestDTO request
    ) {
        return commentService.addComment(postId, userId, request.getContent());
    }
    

    
    @GetMapping
    public List<CommentDTO> getCommentsForPost(@PathVariable Long postId) {
        return commentService.getCommentsForPost(postId);
    }

    
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

   
    @PutMapping("/{commentId}")
    public CommentDTO updateComment(@PathVariable Long commentId, @RequestParam String newContent) {
        return commentService.updateComment(commentId, newContent);
    }

   
    @GetMapping("/count")
    public long getCommentCountForPost(@PathVariable Long postId) {
        return commentService.getCommentCountForPost(postId);
    }
}
