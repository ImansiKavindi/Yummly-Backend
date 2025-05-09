package com.yummly.web.service;

import com.yummly.web.dto.GroupDTO;
import com.yummly.web.model.Group;
import com.yummly.web.model.GroupMembership;
import com.yummly.web.model.User;
import com.yummly.web.repo.GroupMembershipRepo;
import com.yummly.web.repo.GroupRepo;
import com.yummly.web.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepo groupRepo;
    
    @Autowired
    private GroupMembershipRepo membershipRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    // Convert Group entity to GroupDTO
    private GroupDTO convertToDTO(Group group) {
        try {
            GroupDTO dto = new GroupDTO();
            dto.setId(group.getId());
            dto.setName(group.getName());
            dto.setDescription(group.getDescription());
            dto.setCuisineType(group.getCuisineType());
            dto.setImageUrl(group.getImageUrl());
            dto.setCreatedAt(group.getCreatedAt());
            dto.setAdminId(group.getAdmin().getId());
            dto.setAdminName(group.getAdmin().getName());
            dto.setMemberCount(membershipRepo.countByGroup(group));
            return dto;
        } catch (Exception e) {
            logger.error("Error converting Group to DTO: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // Get all groups
    public List<GroupDTO> getAllGroups() {
        try {
            return groupRepo.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all groups: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Get group by ID
    public Optional<GroupDTO> getGroupById(Long id) {
        try {
            return groupRepo.findById(id).map(this::convertToDTO);
        } catch (Exception e) {
            logger.error("Error retrieving group by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    // Create new group
    @Transactional
    public GroupDTO createGroup(GroupDTO groupDTO, Long userId) {
        try {
            User admin;
            try {
                admin = userRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
            } catch (Exception e) {
                // Create a default user if not found
                logger.info("Creating default user with ID: {}", userId);
                admin = new User();
                admin.setId(userId);
                admin.setName("Default User");
                admin.setEmail("default@example.com");
                admin.setPassword("password");
                admin = userRepo.save(admin);
            }
            
            Group group = new Group();
            group.setName(groupDTO.getName());
            group.setDescription(groupDTO.getDescription());
            group.setCuisineType(groupDTO.getCuisineType());
            group.setImageUrl(groupDTO.getImageUrl());
            group.setAdmin(admin);
            
            Group savedGroup = groupRepo.save(group);
            
            // Add admin as the first member
            GroupMembership membership = new GroupMembership();
            membership.setUser(admin);
            membership.setGroup(savedGroup);
            membership.setModerator(true);
            membershipRepo.save(membership);
            
            return convertToDTO(savedGroup);
        } catch (Exception e) {
            logger.error("Error creating group: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // Update group
    @Transactional
    public Optional<GroupDTO> updateGroup(Long groupId, GroupDTO groupDTO, Long userId) {
        try {
            Optional<Group> groupOpt = groupRepo.findById(groupId);
            
            if (groupOpt.isPresent()) {
                Group group = groupOpt.get();
                
                // Check if user is admin
                if (group.getAdmin().getId() != userId) {
                    throw new RuntimeException("Only group admin can update group details");
                }
                
                group.setName(groupDTO.getName());
                group.setDescription(groupDTO.getDescription());
                group.setCuisineType(groupDTO.getCuisineType());
                group.setImageUrl(groupDTO.getImageUrl());
                
                Group updatedGroup = groupRepo.save(group);
                return Optional.of(convertToDTO(updatedGroup));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error updating group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Delete group
    @Transactional
    public boolean deleteGroup(Long groupId, Long userId) {
        try {
            Optional<Group> groupOpt = groupRepo.findById(groupId);
            
            if (groupOpt.isPresent()) {
                Group group = groupOpt.get();
                
                // Check if user is admin
                if (group.getAdmin().getId() != userId) {
                    throw new RuntimeException("Only group admin can delete the group");
                }
                
                groupRepo.delete(group);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error deleting group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Join group
    @Transactional
    public boolean joinGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is already a member
            if (membershipRepo.existsByGroupAndUser(group, user)) {
                return false;
            }
            
            GroupMembership membership = new GroupMembership();
            membership.setUser(user);
            membership.setGroup(group);
            membershipRepo.save(membership);
            
            return true;
        } catch (Exception e) {
            logger.error("Error joining group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Leave group
    @Transactional
    public boolean leaveGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Admin cannot leave group
            if (group.getAdmin().getId() == userId) {
                throw new RuntimeException("Group admin cannot leave the group. Transfer admin role first.");
            }
            
            // Check if user is a member
            if (!membershipRepo.existsByGroupAndUser(group, user)) {
                return false;
            }
            
            membershipRepo.deleteByGroupAndUser(group, user);
            return true;
        } catch (Exception e) {
            logger.error("Error leaving group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Get members of a group
    public List<User> getGroupMembers(Long groupId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            return membershipRepo.findByGroup(group).stream()
                    .map(GroupMembership::getUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting members for group {}: {}", groupId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Get moderators of a group
    public List<User> getGroupModerators(Long groupId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            return membershipRepo.findByGroupAndModeratorIsTrue(group).stream()
                    .map(GroupMembership::getUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting moderators for group {}: {}", groupId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Add moderator
    @Transactional
    public boolean addModerator(Long groupId, Long adminId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            // Check if requester is admin
            if (group.getAdmin().getId() != adminId) {
                throw new RuntimeException("Only group admin can add moderators");
            }
            
            Optional<GroupMembership> membershipOpt = membershipRepo.findByGroupAndUser(
                    group, userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            
            if (membershipOpt.isPresent()) {
                GroupMembership membership = membershipOpt.get();
                membership.setModerator(true);
                membershipRepo.save(membership);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error adding moderator to group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Remove moderator
    @Transactional
    public boolean removeModerator(Long groupId, Long adminId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            // Check if requester is admin
            if (group.getAdmin().getId() != adminId) {
                throw new RuntimeException("Only group admin can remove moderators");
            }
            
            Optional<GroupMembership> membershipOpt = membershipRepo.findByGroupAndUser(
                    group, userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            
            if (membershipOpt.isPresent()) {
                GroupMembership membership = membershipOpt.get();
                membership.setModerator(false);
                membershipRepo.save(membership);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error removing moderator from group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Transfer group ownership
    @Transactional
    public boolean transferOwnership(Long groupId, Long currentAdminId, Long newAdminId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            // Check if requester is current admin
            if (group.getAdmin().getId() != currentAdminId) {
                throw new RuntimeException("Only current admin can transfer ownership");
            }
            
            User newAdmin = userRepo.findById(newAdminId)
                    .orElseThrow(() -> new RuntimeException("New admin user not found"));
            
            // Check if new admin is a member
            if (!membershipRepo.existsByGroupAndUser(group, newAdmin)) {
                throw new RuntimeException("New admin must be a group member");
            }
            
            // Update admin
            group.setAdmin(newAdmin);
            groupRepo.save(group);
            
            // Make sure new admin is a moderator
            Optional<GroupMembership> membershipOpt = membershipRepo.findByGroupAndUser(group, newAdmin);
            if (membershipOpt.isPresent()) {
                GroupMembership membership = membershipOpt.get();
                membership.setModerator(true);
                membershipRepo.save(membership);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error transferring ownership of group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Search groups by name or cuisine type
    public List<GroupDTO> searchGroups(String query) {
        try {
            List<Group> groups = groupRepo.findByNameContainingIgnoreCase(query);
            groups.addAll(groupRepo.findByCuisineType(query));
            
            return groups.stream()
                    .distinct()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching groups: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Get groups by member
    public List<GroupDTO> getGroupsByMember(Long userId) {
        try {
            User user;
            try {
                user = userRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return groupRepo.findGroupsByMember(user).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                logger.warn("User {} not found for getGroupsByMember, returning empty list", userId);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("Error getting groups by member {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Get groups by admin
    public List<GroupDTO> getGroupsByAdmin(Long userId) {
        try {
            User admin;
            try {
                admin = userRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return groupRepo.findByAdmin(admin).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                logger.warn("User {} not found for getGroupsByAdmin, returning empty list", userId);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("Error getting groups by admin {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Check if user is member
    public boolean isMember(Long groupId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return membershipRepo.existsByGroupAndUser(group, user);
        } catch (Exception e) {
            logger.error("Error checking if user {} is member of group {}: {}", userId, groupId, e.getMessage(), e);
            return false;
        }
    }
    
    // Check if user is moderator
    public boolean isModerator(Long groupId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            Optional<GroupMembership> membership = membershipRepo.findByGroupAndUser(
                    group, 
                    userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            
            return membership.map(GroupMembership::isModerator).orElse(false);
        } catch (Exception e) {
            logger.error("Error checking if user {} is moderator of group {}: {}", userId, groupId, e.getMessage(), e);
            return false;
        }
    }
    
    // Check if user is admin
    public boolean isAdmin(Long groupId, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            return group.getAdmin().getId() == userId;
        } catch (Exception e) {
            logger.error("Error checking if user {} is admin of group {}: {}", userId, groupId, e.getMessage(), e);
            return false;
        }
    }
} 