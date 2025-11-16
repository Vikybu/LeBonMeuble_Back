package com.LeBonMeuble.backend.DTO;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
