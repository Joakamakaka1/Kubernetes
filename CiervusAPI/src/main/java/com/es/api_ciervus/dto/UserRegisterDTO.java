package com.es.api_ciervus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegisterDTO {
    private String email;
    private String username;
    private String password1;
    private String password2;
    private String roles;
}
