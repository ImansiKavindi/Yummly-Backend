package com.yummly.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter


public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String password;
    
    // Add explicit getters and setters to resolve compilation issues
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
