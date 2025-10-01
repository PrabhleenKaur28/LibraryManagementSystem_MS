package com.sb_project.book_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequestDTO(
        @NotBlank String isbn,
        @NotBlank String name,
        @NotNull Long authorId,
        @NotBlank String publication,
        String edition,
        @NotNull Integer publicationYear,
        String genre,
        Integer totalCopies,
        Integer availableCopies,
        String shelfLocation,
        String summary
) {}