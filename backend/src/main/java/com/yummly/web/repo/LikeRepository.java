package com.yummly.web.repo;

import com.yummly.web.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPostId(Long postId);
    Like findByPostIdAndUserId(Long postId, Long userId);
}
