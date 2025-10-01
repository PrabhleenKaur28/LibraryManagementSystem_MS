package com.sb_project.book_service.service;

import com.sb_project.book_service.dto.BookRequestDTO;
import com.sb_project.book_service.dto.BookResponseDTO;
import com.sb_project.book_service.exception.BookNotFoundException;

import java.util.List;

public interface BookService {

    List<BookResponseDTO> findAll();

    BookResponseDTO findById(Long id) throws BookNotFoundException;

    BookResponseDTO addNewBook(BookRequestDTO request);

    BookResponseDTO updateBook(Long id, BookRequestDTO request) throws BookNotFoundException;

    void removeById(Long id) throws BookNotFoundException;

    BookResponseDTO borrowBook(Long bookId, Long userId) throws BookNotFoundException;

    BookResponseDTO returnBook(Long bookId, Long userId) throws BookNotFoundException;

    boolean existsByAuthorId(Long authorId);

    List<String> findAllTitlesByAuthorId(Long authorId);

}
