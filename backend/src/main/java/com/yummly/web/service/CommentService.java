package com.yummly.web.service;

import com.yummly.web.dto.CommentDTO;
import com.yummly.web.model.Comment;
import com.yummly.web.model.Post;
import com.yummly.web.model.User;
import com.yummly.web.repo.CommentRepository;
import com.yummly.web.repo.PostRepository;
import com.yummly.web.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepo userRepo;

    // Add comment to a post
    public CommentDTO addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setUsername(user.getName());

        Comment savedComment = commentRepository.save(comment);

        return new CommentDTO(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getUsername(),
                savedComment.getUser().getId()
        );
    }

    // View comments for a post
    public List<CommentDTO> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUsername(),
                        comment.getUser().getId()
                ))
                .collect(Collectors.toList());
    }

    // Delete a comment
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // Update comment content
    public CommentDTO updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));
        comment.setContent(newContent);
        Comment updatedComment = commentRepository.save(comment);
        return new CommentDTO(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedComment.getUsername(),
                updatedComment.getUser().getId()
        );
    }

    // Get comment count for a post
    public long getCommentCountForPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
