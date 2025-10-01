package com.sb_project.book_service.service;

import com.sb_project.book_service.dto.BookRequestDTO;
import com.sb_project.book_service.dto.BookResponseDTO;
import com.sb_project.book_service.entity.Book;
import com.sb_project.book_service.exception.BookNotFoundException;
import com.sb_project.book_service.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<BookResponseDTO> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDTO findById(Long id) throws BookNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return mapToResponseDTO(book);
    }

    @Override
    public BookResponseDTO addNewBook(BookRequestDTO request) {
        // Validate that authorId is provided
        if (request.authorId() == null) {
            throw new RuntimeException("authorId must be provided");
        }

        Book book = mapToEntity(request);
        Book savedBook = bookRepository.save(book);
        return mapToResponseDTO(savedBook);
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) throws BookNotFoundException {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        if (request.authorId() == null) {
            throw new RuntimeException("authorId must be provided");
        }

        // Update all fields
        existingBook.setName(request.name());
        existingBook.setIsbn(request.isbn());
        existingBook.setAuthorId(request.authorId());
        existingBook.setPublication(request.publication());
        existingBook.setEdition(request.edition());
        existingBook.setPublicationYear(request.publicationYear());
        existingBook.setGenre(request.genre());
        existingBook.setTotalCopies(request.totalCopies());
        existingBook.setAvailableCopies(request.availableCopies());
        existingBook.setShelfLocation(request.shelfLocation());
        existingBook.setSummary(request.summary());

        Book savedBook = bookRepository.save(existingBook);
        return mapToResponseDTO(savedBook);
    }

    @Override
    public void removeById(Long id) throws BookNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        Long authorId = book.getAuthorId();
        bookRepository.delete(book);

        try {
            // Optional cleanup in AuthorService if author has no more books
            restTemplate.delete("http://localhost:8081/authors/{id}/check-and-delete", authorId);
        } catch (RestClientException ignored) {}
    }

    @Override
    public BookResponseDTO borrowBook(Long bookId, Long userId) throws BookNotFoundException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available to borrow");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // ✅ Call UserService with correct path variables
        try {
            restTemplate.postForObject(
                    "http://localhost:8082/users/" + userId + "/borrow/" + bookId,
                    null,
                    Void.class
            );
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to update UserService: " + e.getMessage());
        }

        return mapToResponseDTO(book);
    }

    @Override
    public BookResponseDTO returnBook(Long bookId, Long userId) throws BookNotFoundException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        // ✅ Call UserService with correct path variables
        try {
            restTemplate.postForObject(
                    "http://localhost:8082/users/" + userId + "/return/" + bookId,
                    null,
                    Void.class
            );
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to update UserService: " + e.getMessage());
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return mapToResponseDTO(book);
    }

    @Override
    public boolean existsByAuthorId(Long authorId) {
        return bookRepository.existsByAuthorId(authorId);
    }

    @Override
    public List<String> findAllTitlesByAuthorId(Long authorId) {
        return bookRepository.findAllByAuthorId(authorId)
                .stream()
                .map(Book::getName)
                .collect(Collectors.toList());
    }


    /** ---------------- Helper Methods ---------------- **/

    private Book mapToEntity(BookRequestDTO request) {
        Book book = new Book();
        book.setName(request.name());
        book.setIsbn(request.isbn());
        book.setAuthorId(request.authorId());
        book.setPublication(request.publication());
        book.setEdition(request.edition());
        book.setPublicationYear(request.publicationYear());
        book.setGenre(request.genre());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.availableCopies());
        book.setShelfLocation(request.shelfLocation());
        book.setSummary(request.summary());
        return book;
    }

    private BookResponseDTO mapToResponseDTO(Book book) {
        // Fetch author name for response
        String authorName;
        try {
            authorName = restTemplate.getForObject(
                    "http://localhost:8081/authors/{id}/name",
                    String.class,
                    book.getAuthorId()
            );
        } catch (RestClientException e) {
            authorName = "Unknown";
        }

        String availabilityStatus;
        if (book.getTotalCopies() == null || book.getAvailableCopies() == null) {
            availabilityStatus = "Unknown";
        } else if (book.getAvailableCopies() > 0) {
            availabilityStatus = "Available";
        } else {
            availabilityStatus = "Not Available";
        }

        return new BookResponseDTO(
                book.getBookId(),
                book.getName(),
                book.getIsbn(),
                authorName,
                book.getPublication(),
                book.getEdition(),
                book.getGenre(),
                book.getPublicationYear(),
                availabilityStatus,
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getShelfLocation(),
                book.getSummary(),
                book.getAddedAt()
        );
    }
}
