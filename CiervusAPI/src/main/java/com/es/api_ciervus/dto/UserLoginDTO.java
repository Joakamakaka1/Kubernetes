package com.es.api_ciervus.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginDTO {
    private String username;
    private String password;
}
