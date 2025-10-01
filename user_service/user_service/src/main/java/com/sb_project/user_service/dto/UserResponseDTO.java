package com.sb_project.user_service.dto;

import java.util.List;

public record UserResponseDTO(
        Long userId,
        String fullName,
        String email,
        String phoneNumber,
        String membershipType,
        List<String> borrowedBookTitles
) {}
