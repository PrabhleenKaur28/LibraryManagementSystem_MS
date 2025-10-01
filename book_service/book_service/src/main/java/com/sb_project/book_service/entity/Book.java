package com.sb_project.book_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "publication", nullable = false)
    private String publication;

    @Column(name = "edition")
    private String edition;

    @Column(name = "genre")
    private String genre;

    @Column(name = "shelf_location")
    private String shelfLocation;

    @Column(name = "availability_status")
    private String availabilityStatus;

    @Column(name = "total_copies")
    private Integer totalCopies;

    @Column(name = "available_copies")
    private Integer availableCopies;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;
}
