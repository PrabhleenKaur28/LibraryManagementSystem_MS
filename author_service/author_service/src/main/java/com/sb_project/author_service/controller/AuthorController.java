package com.sb_project.author_service.controller;

import com.sb_project.author_service.dto.AuthorResponseDTO;
import com.sb_project.author_service.entity.Author;
import com.sb_project.author_service.exception.AuthorNotFoundException;
import com.sb_project.author_service.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDTO>> getAllAuthors() {
        List<AuthorResponseDTO> authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) throws AuthorNotFoundException {
        AuthorResponseDTO authorDTO = authorService.findById(id)
                .map(authorService::mapToResponseDTO)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
        return ResponseEntity.ok(authorDTO);
    }

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> addAuthor(@Valid @RequestBody Author authorRequest) {
        AuthorResponseDTO savedAuthorDTO = authorService.addAuthor(authorRequest);
        return ResponseEntity.ok(savedAuthorDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author updatedAuthor) throws AuthorNotFoundException {
        Author author = authorService.updateAuthor(id, updatedAuthor);
        return ResponseEntity.ok(authorService.mapToResponseDTO(author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) throws AuthorNotFoundException {
        boolean deleted = authorService.deleteIfNoBooks(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(409).body(
                    Map.of(
                            "status", 409,
                            "message", "Cannot delete author: books exist for this author",
                            "path", "/authors/" + id
                    )
            );
        }
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<?> handleAuthorNotFound(AuthorNotFoundException ex) {
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", 404,
                        "message", ex.getMessage(),
                        "path", "/authors"
                )
        );
    }
}
