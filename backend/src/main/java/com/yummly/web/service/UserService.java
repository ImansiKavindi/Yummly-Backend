package com.yummly.web.service;

import com.yummly.web.dto.UserDTO;
import com.yummly.web.model.User;
import com.yummly.web.repo.UserRepo;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<UserDTO> getAllUsers(){
        List<User>userList = userRepo.findAll();
        return modelMapper.map(userList,new TypeToken<List<UserDTO>>(){}.getType());
    }


     public UserDTO saveUser(UserDTO userDTO) {
        userRepo.save(modelMapper.map(userDTO,User.class));
        return userDTO;
     }

     public UserDTO updateUser(UserDTO userDTO){
        userRepo.save(modelMapper.map(userDTO,User.class));
        return userDTO;
     }


    public String deleteUser(UserDTO userDTO){
        userRepo.delete(modelMapper.map(userDTO ,User.class));
        return "User Deleted";
    }

    public User getUserByEmail(String email) {

        return userRepo.findByEmail(email);
    }

    public User login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        return user;
    }
}
