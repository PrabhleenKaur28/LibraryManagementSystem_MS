package com.sb_project.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank String fullName,
        @Email String email,
        String phoneNumber,
        @NotBlank String membershipType
) {}
