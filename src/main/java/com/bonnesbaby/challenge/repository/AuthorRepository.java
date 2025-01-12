package com.bonnesbaby.challenge.repository;


import com.bonnesbaby.challenge.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
