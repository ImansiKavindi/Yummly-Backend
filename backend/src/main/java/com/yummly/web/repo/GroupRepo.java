package com.yummly.web.repo;

import com.yummly.web.model.Group;
import com.yummly.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    List<Group> findByCuisineType(String cuisineType);
    
    List<Group> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT g FROM Group g JOIN g.memberships m WHERE m.user = ?1")
    List<Group> findGroupsByMember(User user);
    
    List<Group> findByAdmin(User admin);
} 