package com.yummly.web.repo;

import com.yummly.web.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
    List<Like> findByPostId(Long postId);
    long countByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
