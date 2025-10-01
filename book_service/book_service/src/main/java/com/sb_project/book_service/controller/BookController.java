package com.sb_project.book_service.controller;

import com.sb_project.book_service.dto.BookRequestDTO;
import com.sb_project.book_service.dto.BookResponseDTO;
import com.sb_project.book_service.exception.BookNotFoundException;
import com.sb_project.book_service.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")

public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable long id) throws BookNotFoundException {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> addNewBook(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO savedBook = bookService.addNewBook(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO request) throws BookNotFoundException {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable("id") Long bookId) throws BookNotFoundException {
        bookService.removeById(bookId);
    }

    @PostMapping("/{bookId}/borrow/{userId}")
    public BookResponseDTO borrowBook(@PathVariable Long bookId, @PathVariable Long userId) throws BookNotFoundException {
        return bookService.borrowBook(bookId, userId);
    }

    @PostMapping("/{bookId}/return/{userId}")
    public BookResponseDTO returnBook(@PathVariable Long bookId, @PathVariable Long userId) throws BookNotFoundException {
        return bookService.returnBook(bookId, userId);
    }

    @GetMapping("/author/{id}/exists")
    public ResponseEntity<Boolean> checkAuthorHasBooks(@PathVariable Long id) {
        boolean hasBooks = bookService.existsByAuthorId(id);
        return ResponseEntity.ok(hasBooks);
    }

    @GetMapping("/author/{id}/titles")
    public ResponseEntity<List<String>> getAuthorBookTitles(@PathVariable Long id) {
        List<String> titles = bookService.findAllTitlesByAuthorId(id);
        return ResponseEntity.ok(titles);
    }


    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<?> handleBookNotFound(BookNotFoundException ex) {
        // Create a simple JSON response
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", 404,
                        "message", ex.getMessage(),
                        "path", "/books"
                )
        );
    }
}
