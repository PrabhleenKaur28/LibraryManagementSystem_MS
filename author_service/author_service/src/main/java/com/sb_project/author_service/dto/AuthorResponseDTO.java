package com.sb_project.author_service.dto;

import java.util.List;

public record AuthorResponseDTO(
        Long authorId,
        String fullName,
        List<String> bookTitles
) {}

