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

}
