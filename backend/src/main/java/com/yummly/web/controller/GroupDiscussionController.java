package com.yummly.web.controller;

import com.yummly.web.model.GroupDiscussion;
import com.yummly.web.service.GroupDiscussionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/discussions")
@CrossOrigin
public class GroupDiscussionController {

    private static final Logger logger = LoggerFactory.getLogger(GroupDiscussionController.class);

    @Autowired
    private GroupDiscussionService discussionService;
    
    // Get all discussions for a group
    @GetMapping
    public ResponseEntity<List<GroupDiscussion>> getGroupDiscussions(@PathVariable Long groupId) {
        try {
            logger.info("Getting discussions for group: {}", groupId);
            List<GroupDiscussion> discussions = discussionService.getDiscussionsByGroup(groupId);
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            logger.error("Error in getGroupDiscussions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get a specific discussion
    @GetMapping("/{discussionId}")
    public ResponseEntity<GroupDiscussion> getDiscussion(
            @PathVariable Long groupId,
            @PathVariable Long discussionId) {
        try {
            return discussionService.getDiscussionById(discussionId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error in getDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Create a new discussion
    @PostMapping
    public ResponseEntity<GroupDiscussion> createDiscussion(
            @PathVariable Long groupId,
            @RequestBody GroupDiscussion discussion,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        try {
            logger.info("Creating discussion in group {} for user {}", groupId, userId);
            GroupDiscussion createdDiscussion = discussionService.createDiscussion(groupId, discussion, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscussion);
        } catch (RuntimeException e) {
            logger.error("Error in createDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error in createDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update a discussion
    @PutMapping("/{discussionId}")
    public ResponseEntity<GroupDiscussion> updateDiscussion(
            @PathVariable Long groupId,
            @PathVariable Long discussionId,
            @RequestBody GroupDiscussion discussion,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        try {
            return discussionService.updateDiscussion(discussionId, discussion, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            logger.error("Error in updateDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error in updateDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete a discussion
    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable Long groupId,
            @PathVariable Long discussionId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        try {
            boolean deleted = discussionService.deleteDiscussion(discussionId, userId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            logger.error("Error in deleteDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error in deleteDiscussion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Search discussions in a group
    @GetMapping("/search")
    public ResponseEntity<List<GroupDiscussion>> searchDiscussions(
            @PathVariable Long groupId,
            @RequestParam String keyword) {
        try {
            List<GroupDiscussion> discussions = discussionService.searchDiscussions(groupId, keyword);
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            logger.error("Error in searchDiscussions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 