package com.es.api_ciervus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDTO {
    private String email;
    private String username;
    private String password;
    private String roles;
}
