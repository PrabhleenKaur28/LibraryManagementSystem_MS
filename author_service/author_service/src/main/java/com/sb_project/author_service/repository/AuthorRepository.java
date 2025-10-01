package com.sb_project.author_service.repository;

import com.sb_project.author_service.entity.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    List<Author> findAll();
}
