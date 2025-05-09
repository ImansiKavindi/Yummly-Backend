package com.yummly.web.repo;

import com.yummly.web.model.Group;
import com.yummly.web.model.GroupDiscussion;
import com.yummly.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDiscussionRepo extends JpaRepository<GroupDiscussion, Long> {
    List<GroupDiscussion> findByGroup(Group group);
    
    List<GroupDiscussion> findByUser(User user);
    
    List<GroupDiscussion> findByGroupOrderByCreatedAtDesc(Group group);
    
    List<GroupDiscussion> findByGroupAndTitleContainingIgnoreCase(Group group, String keyword);
} 