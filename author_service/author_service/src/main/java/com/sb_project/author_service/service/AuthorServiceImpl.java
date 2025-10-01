package com.sb_project.author_service.service;

import com.sb_project.author_service.dto.AuthorResponseDTO;
import com.sb_project.author_service.entity.Author;
import com.sb_project.author_service.exception.AuthorNotFoundException;
import com.sb_project.author_service.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final RestTemplate restTemplate;

    @Override
    public List<AuthorResponseDTO> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public Optional<Author> findById(Long authorId) throws AuthorNotFoundException {
        return Optional.of(authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + authorId)));
    }

    @Override
    public Optional<Author> findByName(String name) {
        return authorRepository.findAll()
                .stream()
                .filter(author -> author.getFullName().equalsIgnoreCase(name))
                .findFirst();
    }


    @Override
    public AuthorResponseDTO addAuthor(Author author) {
        Author savedAuthor = authorRepository.save(author);
        return mapToResponseDTO(savedAuthor);
    }


    @Override
    public Author updateAuthor(Long authorId, Author updatedAuthor) throws AuthorNotFoundException {
        Author existingAuthor = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + authorId));

        existingAuthor.setFullName(updatedAuthor.getFullName());
        existingAuthor.setDateOfBirth(updatedAuthor.getDateOfBirth());
        existingAuthor.setNationality(updatedAuthor.getNationality());

        return authorRepository.save(existingAuthor);
    }

    @Override
    public boolean deleteIfNoBooks(Long authorId) throws AuthorNotFoundException {
        // 1️⃣ Fetch author or throw exception
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + authorId));

        // 2️⃣ Check with BookService if author has any books
        Boolean hasBooks = restTemplate.getForObject(
                "http://localhost:8080/books/author/{id}/exists",
                Boolean.class,
                authorId
        );

        // 3️⃣ Delete author if no books exist
        if (hasBooks != null && !hasBooks) {
            authorRepository.delete(author);
            return true;
        }

        return false; // author has books, no deletion
    }



    @Override
    public AuthorResponseDTO mapToResponseDTO(Author author) {
        List<String> bookTitles;
        try {
            bookTitles = restTemplate.exchange(
                    "http://localhost:8080/books/author/{id}/titles",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {},
                    author.getAuthor_Id()
            ).getBody();
        } catch (RestClientException e) {
            bookTitles = List.of();
        }

        return new AuthorResponseDTO(
                author.getAuthor_Id(),
                author.getFullName(),
                bookTitles != null ? bookTitles : List.of()
        );
    }



}
