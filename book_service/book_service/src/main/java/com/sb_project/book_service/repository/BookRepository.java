package com.sb_project.book_service.repository;

import com.sb_project.book_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByAuthorId(Long authorId);

    List<Book> findAllByAuthorId(Long authorId);
}

