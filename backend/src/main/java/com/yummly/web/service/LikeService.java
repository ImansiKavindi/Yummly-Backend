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

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepo userRepo;

    public Like addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (post != null && user != null) {
            Like existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
            if (existingLike != null) {
                return null; // Already liked
            }
            Like like = new Like(user, post);
            return likeRepository.save(like);
        }
        return null;
    }

    public void removeLike(Long postId, Long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId);
        if (like != null) {
            likeRepository.delete(like);
        }
    }

    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }
}
