package com.yummly.web.controller;


import com.yummly.web.dto.UserDTO;
import com.yummly.web.model.User;
import com.yummly.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yummly.web.dto.LoginRequest;


import java.util.List;

@RestController


@RequestMapping(value = "api/v1/")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/getUsers")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/adduser")
    public UserDTO saveUser(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @PutMapping("/updateuser")
    public UserDTO updateUser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/deleteuser")
    public String deleteuser(@RequestBody UserDTO userDTO) {
        return userService.deleteUser(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        System.out.println("Login attempt with email: " + userDTO.getEmail());

        User existingUser = userService.getUserByEmail(userDTO.getEmail());

        if (existingUser != null) {
            System.out.println("User found: " + existingUser.getEmail());

            if (existingUser.getPassword().equals(userDTO.getPassword())) {
                System.out.println("Login successful");
                return ResponseEntity.ok(existingUser);
            } else {
                System.out.println("Incorrect password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }



}




