package com.yummly.web.service;

import com.yummly.web.model.Like;
import com.yummly.web.model.Post;
import com.yummly.web.model.User;
import com.yummly.web.repo.LikeRepository;
import com.yummly.web.repo.PostRepository;
import com.yummly.web.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepo userRepo;

    // Toggle like/unlike by user
    public boolean toggleLike(Long postId, Long userId) {
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;  // Unliked
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return true;  // Liked
        }
    }

    // Check if a user has liked a post
    public boolean hasUserLiked(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // Get total like count for a post
    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // Get all likes for a post
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }
}
