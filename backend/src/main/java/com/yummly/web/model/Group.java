package com.yummly.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false, name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMembership> memberships = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupDiscussion> discussions = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isAdmin(User user) {
        return admin.getId() == user.getId();
    }
    
    public void addMember(User user) {
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(this);
        membership.setJoinedAt(LocalDateTime.now());
        memberships.add(membership);
    }
    
    public void removeMember(User user) {
        memberships.removeIf(membership -> membership.getUser().getId() == user.getId());
    }
    
    // Explicit getters and setters to fix Lombok issues
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCuisineType() {
        return cuisineType;
    }
    
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getAdmin() {
        return admin;
    }
    
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    
    public List<GroupMembership> getMemberships() {
        return memberships;
    }
    
    public void setMemberships(List<GroupMembership> memberships) {
        this.memberships = memberships;
    }
    
    public List<GroupDiscussion> getDiscussions() {
        return discussions;
    }
    
    public void setDiscussions(List<GroupDiscussion> discussions) {
        this.discussions = discussions;
    }
} 