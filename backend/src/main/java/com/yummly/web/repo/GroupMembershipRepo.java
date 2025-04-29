package com.yummly.web.repo;

import com.yummly.web.model.Group;
import com.yummly.web.model.GroupMembership;
import com.yummly.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembershipRepo extends JpaRepository<GroupMembership, Long> {
    List<GroupMembership> findByGroup(Group group);
    
    List<GroupMembership> findByUser(User user);
    
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);
    
    boolean existsByGroupAndUser(Group group, User user);
    
    void deleteByGroupAndUser(Group group, User user);
    
    List<GroupMembership> findByGroupAndModeratorIsTrue(Group group);
    
    int countByGroup(Group group);
} 