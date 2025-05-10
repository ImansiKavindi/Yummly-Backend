package com.yummly.web.service;

import com.yummly.web.dto.GroupMessageDTO;
import com.yummly.web.model.Group;
import com.yummly.web.model.GroupMessage;
import com.yummly.web.model.User;
import com.yummly.web.repo.GroupMessageRepo;
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
public class GroupMessageService {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageService.class);

    @Autowired
    private GroupMessageRepo messageRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GroupService groupService;

    // Convert GroupMessage entity to GroupMessageDTO
    private GroupMessageDTO convertToDTO(GroupMessage message) {
        GroupMessageDTO dto = new GroupMessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setGroupId(message.getGroup().getId());
        dto.setUserId(message.getUser().getId());
        dto.setUserName(message.getUser().getName());
        return dto;
    }

    // Get messages for a group
    public List<GroupMessageDTO> getGroupMessages(Long groupId) {
        try {
            return messageRepo.findByGroupIdOrderByCreatedAtDesc(groupId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving messages for group {}: {}", groupId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Create a new message
    @Transactional
    public GroupMessageDTO createMessage(Long groupId, String content, Long userId) {
        try {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is a member of the group
            if (!groupService.isMember(groupId, userId)) {
                throw new RuntimeException("Only group members can send messages");
            }
            
            GroupMessage message = new GroupMessage();
            message.setContent(content);
            message.setGroup(group);
            message.setUser(user);
            
            GroupMessage savedMessage = messageRepo.save(message);
            return convertToDTO(savedMessage);
        } catch (Exception e) {
            logger.error("Error creating message in group {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // Delete a message
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        try {
            Optional<GroupMessage> messageOpt = messageRepo.findById(messageId);
            
            if (messageOpt.isPresent()) {
                GroupMessage message = messageOpt.get();
                Group group = message.getGroup();
                
                // Check if user is the message author, a moderator, or the group creator
                if (userId.equals(message.getUser().getId()) || 
                    groupService.isModerator(group.getId(), userId) || 
                    groupService.isCreator(group.getId(), userId)) {
                    
                    messageRepo.delete(message);
                    return true;
                } else {
                    throw new RuntimeException("You don't have permission to delete this message");
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error deleting message {}: {}", messageId, e.getMessage(), e);
            throw e;
        }
    }
} 