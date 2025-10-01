package com.sb_project.book_service.dto;

import java.time.LocalDateTime;

public record BookResponseDTO(
        Long bookId,
        String name,
        String isbn,
        String authorName,
        String publication,
        String edition,
        String genre,
        Integer publicationYear,
        String availabilityStatus,
        Integer totalCopies,
        Integer availableCopies,
        String shelfLocation,
        String summary,
        LocalDateTime addedAt
) {}
