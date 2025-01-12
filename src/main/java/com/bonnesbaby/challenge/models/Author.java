package com.bonnesbaby.challenge.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria generada automáticamente

    @Column(nullable = false,columnDefinition = "TEXT")
    private String name;

    @Column(name = "birth_year")
    private String birth_year;

    @Column(name = "death_year")
    private String death_year;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>(); // Relación con los libros

    public Author() {} // Constructor vacío requerido por JPA

    public Author(String name, String birth_year, String death_year) {
        this.name = name;
        this.birth_year = birth_year;
        this.death_year = death_year;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getbirth_year() {
        return birth_year;
    }

    public String getdeath_year() {
        return death_year;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
}
