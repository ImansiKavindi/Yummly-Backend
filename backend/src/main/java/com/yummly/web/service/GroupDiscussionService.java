package com.yummly.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yummly.web.model.Group;
import com.yummly.web.model.GroupDiscussion;
import com.yummly.web.model.User;
import com.yummly.web.repo.GroupDiscussionRepo;
import com.yummly.web.repo.GroupRepo;
import com.yummly.web.repo.UserRepo;

@Service
public class GroupDiscussionService {

    private static final Logger logger = LoggerFactory.getLogger(GroupDiscussionService.class);

    @Autowired
    private GroupDiscussionRepo discussionRepo;
    
    @Autowired
    private GroupRepo groupRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private GroupService groupService;
    
    // Get all discussions for a group
    public List<GroupDiscussion> getDiscussionsByGroup(Long groupId) {
        try {
            Optional<Group> group = groupRepo.findById(groupId);
            if (group.isPresent()) {
                return discussionRepo.findByGroupOrderByCreatedAtDesc(group.get());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error getting discussions for group {}: {}", groupId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Get discussion by ID
    public Optional<GroupDiscussion> getDiscussionById(Long discussionId) {
        try {
            return discussionRepo.findById(discussionId);
        } catch (Exception e) {
            logger.error("Error getting discussion by ID {}: {}", discussionId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    // Create a new discussion
    @Transactional
    public GroupDiscussion createDiscussion(Long groupId, GroupDiscussion discussion, Long userId) {
        try {
            // Get group
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            // Get user
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is member of the group
            if (!groupService.isMember(groupId, userId)) {
                throw new RuntimeException("Only group members can create discussions");
            }
            
            // Set references
            discussion.setGroup(group);
            discussion.setUser(user);
            
            return discussionRepo.save(discussion);
        } catch (Exception e) {
            logger.error("Error creating discussion for group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Update a discussion
    @Transactional
    public Optional<GroupDiscussion> updateDiscussion(Long discussionId, GroupDiscussion updatedDiscussion, Long userId) {
        try {
            Optional<GroupDiscussion> existingDiscussion = discussionRepo.findById(discussionId);
            
            if (existingDiscussion.isPresent()) {
                GroupDiscussion discussion = existingDiscussion.get();
                
                // Check if user is the author of the discussion
                if (discussion.getUser().getId() != userId) {
                    throw new RuntimeException("Only the author can update the discussion");
                }
                
                // Update fields
                discussion.setTitle(updatedDiscussion.getTitle());
                discussion.setContent(updatedDiscussion.getContent());
                
                return Optional.of(discussionRepo.save(discussion));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error updating discussion {}: {}", discussionId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Delete a discussion
    @Transactional
    public boolean deleteDiscussion(Long discussionId, Long userId) {
        try {
            Optional<GroupDiscussion> discussionOpt = discussionRepo.findById(discussionId);
            
            if (discussionOpt.isPresent()) {
                GroupDiscussion discussion = discussionOpt.get();
                Group group = discussion.getGroup();
                
                // Check if user is the author or an admin/moderator
                boolean isAuthor = discussion.getUser().getId() == userId;
                boolean isAdmin = groupService.isAdmin(group.getId(), userId);
                boolean isModerator = groupService.isModerator(group.getId(), userId);
                
                if (isAuthor || isAdmin || isModerator) {
                    discussionRepo.delete(discussion);
                    return true;
                } else {
                    throw new RuntimeException("You don't have permission to delete this discussion");
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error deleting discussion {}: {}", discussionId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Search discussions in a group
    public List<GroupDiscussion> searchDiscussions(Long groupId, String keyword) {
        try {
            Optional<Group> group = groupRepo.findById(groupId);
            
            if (group.isPresent()) {
                return discussionRepo.findByGroupAndTitleContainingIgnoreCase(group.get(), keyword);
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error searching discussions in group {}: {}", groupId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
} 