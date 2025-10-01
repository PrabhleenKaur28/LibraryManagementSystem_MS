package com.sb_project.author_service.service;

import com.sb_project.author_service.dto.AuthorResponseDTO;
import com.sb_project.author_service.entity.Author;
import com.sb_project.author_service.exception.AuthorNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<AuthorResponseDTO> findAll();

    Optional<Author> findById(Long authorId) throws AuthorNotFoundException;

    Optional<Author> findByName(String name);

    AuthorResponseDTO addAuthor(Author author);

    Author updateAuthor(Long authorId, Author updatedAuthor) throws AuthorNotFoundException;

    boolean deleteIfNoBooks(Long authorId) throws AuthorNotFoundException;

    AuthorResponseDTO mapToResponseDTO(Author author);

}
