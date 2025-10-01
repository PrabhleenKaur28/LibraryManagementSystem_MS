package com.sb_project.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    private String membershipType;

    @ElementCollection
    @CollectionTable(name = "user_borrowed_books", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "book_id")
    private List<Long> borrowedBookIds = new ArrayList<>();
}
