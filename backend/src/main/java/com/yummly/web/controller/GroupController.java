package com.yummly.web.controller;

import com.yummly.web.dto.GroupDTO;
import com.yummly.web.dto.GroupMessageDTO;
import com.yummly.web.model.User;
import com.yummly.web.service.GroupMessageService;
import com.yummly.web.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;
    
    @Autowired
    private GroupMessageService messageService;
    
    // Get all groups
    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        try {
            return ResponseEntity.ok(groupService.getAllGroups());
        } catch (Exception e) {
            logger.error("Error in getAllGroups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get group by ID
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long groupId) {
        try {
            return groupService.getGroupById(groupId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error in getGroupById: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Create a new group
    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(
            @RequestBody GroupDTO groupDTO,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            logger.info("Creating group with name: {}, for user: {}", groupDTO.getName(), userId);
            GroupDTO createdGroup = groupService.createGroup(groupDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
        } catch (Exception e) {
            logger.error("Error in createGroup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update group
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupDTO groupDTO,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            return groupService.updateGroup(groupId, groupDTO, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error in updateGroup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete group
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "userid", defaultValue = "1") Long headerUserId) {
        
        try {
            // Use userId from query parameter if provided, otherwise from header
            Long effectiveUserId = userId != null ? userId : headerUserId;
            logger.info("Deleting group {} requested by user {}", groupId, effectiveUserId);
            
            boolean deleted = groupService.deleteGroup(groupId, effectiveUserId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error in deleteGroup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Alternative endpoint for deleting groups using POST
    @PostMapping("/{groupId}/delete")
    public ResponseEntity<Void> deleteGroupViaPost(
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> request,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            logger.info("Deleting group {} via POST endpoint by user {}", groupId, userId);
            boolean deleted = groupService.deleteGroup(groupId, userId);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error in deleteGroupViaPost: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Join group
    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(
            @PathVariable Long groupId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            boolean joined = groupService.joinGroup(groupId, userId);
            return joined ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error in joinGroup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Leave group
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long groupId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            boolean left = groupService.leaveGroup(groupId, userId);
            return left ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error in leaveGroup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get members
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable Long groupId) {
        try {
            return ResponseEntity.ok(groupService.getGroupMembers(groupId));
        } catch (Exception e) {
            logger.error("Error in getGroupMembers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get moderators
    @GetMapping("/{groupId}/moderators")
    public ResponseEntity<List<User>> getGroupModerators(@PathVariable Long groupId) {
        try {
            return ResponseEntity.ok(groupService.getGroupModerators(groupId));
        } catch (Exception e) {
            logger.error("Error in getGroupModerators: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Add moderator
    @PostMapping("/{groupId}/moderator/{userId}")
    public ResponseEntity<?> addModerator(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam Long creatorId) {
        try {
            if (!groupService.isCreator(groupId, creatorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only group creator can add moderators");
            }
            boolean success = groupService.addModerator(groupId, creatorId, userId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error adding moderator to group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Remove moderator
    @DeleteMapping("/{groupId}/moderator/{userId}")
    public ResponseEntity<?> removeModerator(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam Long creatorId) {
        try {
            if (!groupService.isCreator(groupId, creatorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only group creator can remove moderators");
            }
            boolean success = groupService.removeModerator(groupId, creatorId, userId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error removing moderator from group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Transfer ownership
    @PostMapping("/{groupId}/transfer-ownership")
    public ResponseEntity<Void> transferOwnership(
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> request,
            @RequestHeader(value = "userid", defaultValue = "1") Long creatorId) {
        
        try {
            Long newCreatorId = request.get("newCreatorId");
            if (newCreatorId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            boolean transferred = groupService.transferOwnership(groupId, creatorId, newCreatorId);
            return transferred ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error in transferOwnership: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Search groups
    @GetMapping("/search")
    public ResponseEntity<List<GroupDTO>> searchGroups(@RequestParam String query) {
        try {
            return ResponseEntity.ok(groupService.searchGroups(query));
        } catch (Exception e) {
            logger.error("Error in searchGroups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get groups by member
    @GetMapping("/member/{userId}")
    public ResponseEntity<List<GroupDTO>> getGroupsByMember(
            @PathVariable Long userId,
            @RequestHeader(value = "userid", defaultValue = "1") Long requestUserId) {
        try {
            return ResponseEntity.ok(groupService.getGroupsByMember(userId));
        } catch (Exception e) {
            logger.error("Error in getGroupsByMember: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get groups by creator
    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<GroupDTO>> getGroupsByCreator(@PathVariable Long userId) {
        try {
            List<GroupDTO> groups = groupService.getGroupsByCreator(userId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error getting groups by creator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Check if user is member
    @GetMapping("/{groupId}/is-member")
    public ResponseEntity<Boolean> isMember(
            @PathVariable Long groupId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            return ResponseEntity.ok(groupService.isMember(groupId, userId));
        } catch (Exception e) {
            logger.error("Error in isMember: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Check if user is moderator
    @GetMapping("/{groupId}/is-moderator")
    public ResponseEntity<Boolean> isModerator(
            @PathVariable Long groupId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            return ResponseEntity.ok(groupService.isModerator(groupId, userId));
        } catch (Exception e) {
            logger.error("Error in isModerator: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Check if user is creator
    @GetMapping("/{groupId}/is-creator")
    public ResponseEntity<Boolean> isCreator(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            boolean isCreator = groupService.isCreator(groupId, userId);
            return ResponseEntity.ok(isCreator);
        } catch (Exception e) {
            logger.error("Error checking if user is creator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Remove member
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestHeader(value = "userid", defaultValue = "1") Long requesterId) {
        try {
            // Check if requester is creator or moderator
            if (!groupService.isCreator(groupId, requesterId) && 
                !groupService.isModerator(groupId, requesterId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Creator cannot be removed
            if (groupService.isCreator(groupId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            boolean removed = groupService.removeMember(groupId, userId);
            return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error removing member {} from group {}: {}", userId, groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get messages for a group
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessageDTO>> getGroupMessages(
            @PathVariable Long groupId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            if (!groupService.isMember(groupId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(messageService.getGroupMessages(groupId));
        } catch (Exception e) {
            logger.error("Error getting messages for group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Create a new message
    @PostMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessageDTO> createMessage(
            @PathVariable Long groupId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (!groupService.isMember(groupId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            GroupMessageDTO message = messageService.createMessage(groupId, content, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            logger.error("Error creating message in group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete a message
    @DeleteMapping("/{groupId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long groupId,
            @PathVariable Long messageId,
            @RequestHeader(value = "userid", defaultValue = "1") Long userId) {
        
        try {
            if (!groupService.isMember(groupId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            boolean deleted = messageService.deleteMessage(messageId, userId);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting message {} from group {}: {}", messageId, groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 