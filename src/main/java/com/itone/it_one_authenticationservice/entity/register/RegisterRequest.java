package com.itone.it_one_authenticationservice.entity.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String mail;
    private String firstName;
    private String lastName;
    private String password;
}