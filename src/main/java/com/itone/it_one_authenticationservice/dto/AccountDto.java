package com.itone.it_one_authenticationservice.dto;


import com.itone.it_one_authenticationservice.entity.Role;

public record AccountDto(
        Long id,
        String mail,
        String firstName,
        String lastName,
        Role role
) {
}