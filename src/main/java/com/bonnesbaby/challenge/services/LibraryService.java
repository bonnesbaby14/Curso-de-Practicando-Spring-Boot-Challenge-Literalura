package com.bonnesbaby.challenge.services;


import com.bonnesbaby.challenge.models.Author;
import com.bonnesbaby.challenge.models.Book;
import com.bonnesbaby.challenge.repository.AuthorRepository;
import com.bonnesbaby.challenge.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {


    private AuthorRepository authorRepository;

    private BookRepository bookRepository;


    public LibraryService(AuthorRepository authorRepository,BookRepository bookRepository) {

        this.authorRepository=authorRepository;
        this.bookRepository=bookRepository;
    }

    @Transactional
    public Author saveAuthorWithBooks(Author author) {
        // Guardar el autor y sus libros
        for (Book book : author.getBooks()) {
            book.setAuthor(author);
        }
        return authorRepository.save(author);
    }

    @Transactional
    public Book saveBook(Book book) {
        // Guardar un libro con su autor asociado
        return bookRepository.save(book);
    }
}