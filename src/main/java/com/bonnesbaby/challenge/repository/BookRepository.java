package com.bonnesbaby.challenge.repository;

import com.bonnesbaby.challenge.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}