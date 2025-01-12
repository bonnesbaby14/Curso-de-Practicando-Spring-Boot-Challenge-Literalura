package com.bonnesbaby.challenge.menu;


import com.bonnesbaby.challenge.models.*;
import com.bonnesbaby.challenge.repository.AuthorRepository;
import com.bonnesbaby.challenge.repository.BookRepository;
import com.bonnesbaby.challenge.services.ConsumoAPI;
import com.bonnesbaby.challenge.services.ConvierteDatos;
import com.bonnesbaby.challenge.services.LibraryService;

import java.util.Scanner;

public class Menu {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();


    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;


    public Menu(AuthorRepository authorRepository,BookRepository bookRepository) {
        this.authorRepository=authorRepository;
        this.bookRepository=bookRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarbookWeb();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    System.out.println("Ingresa el año: ");
                    int anio = teclado.nextInt();
                    listarAutoresVivosEnAnio(anio);
                    break;
                case 5:
                    System.out.println("Ingresa el idioma: ");
                    String idioma = teclado.nextLine();
                    listarLibrosPorIdioma(idioma);
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosBookResponse getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") );
        System.out.println(json);
        DatosBookResponse datos = conversor.obtenerDatos(json, DatosBookResponse.class);
        return datos;
    }
    private void buscarbookWeb() {
        DatosBookResponse datos = getDatosSerie(); // Obtener datos desde una API o fuente externa
        if (datos != null && datos.results() != null && !datos.results().isEmpty()) {
            for (DatosBook libro : datos.results()) {
                // Mostrar los detalles del libro
                System.out.println("\n--- Libro Encontrado ---");
                System.out.println("Título: " + libro.title());

                // Imprimir los autores
                if (libro.datosAuthors() != null && !libro.datosAuthors().isEmpty()) {
                    System.out.println("Autores:");
                    for (DatosAuthor datosAuthor : libro.datosAuthors()) {
                        System.out.println(" - " + datosAuthor.name() + " Anio nacimiento: "+datosAuthor.birth_year()+ " Anio fallecimiento: "+datosAuthor.death_year());
                        System.out.println(" - " + datosAuthor.name());

                    }
                } else {
                    System.out.println("Autor: No disponible");
                }

                // Convertir los datos a entidades JPA
                Author authorEntity = null;

                if (libro.datosAuthors() != null && !libro.datosAuthors().isEmpty()) {
                    for (DatosAuthor datosAuthor : libro.datosAuthors()) {
                        authorEntity = new Author(
                                datosAuthor.name(),
                                datosAuthor.birth_year() ,
                                datosAuthor.death_year()
                        );
                    }
                }

                // Crear una instancia del libro
                Book bookEntity = new Book(
                        libro.title(),
                        libro.languages(),
                        libro.downloadCount()
                );

                // Asociar el autor al libro si existe
                if (authorEntity != null) {
                    authorEntity.addBook(bookEntity); // Establecer la relación entre autor y libro
                }

                LibraryService libraryService=new LibraryService(authorRepository,bookRepository);
                // Guardar en la base de datos utilizando el servicio
                if (authorEntity != null) {
                    libraryService.saveAuthorWithBooks(authorEntity);
                } else {
                    libraryService.saveBook(bookEntity); // Guardar libro sin autor
                }

                // Confirmación de guardado
                System.out.println("Libro guardado en la base de datos.");
            }
        } else {
            System.out.println("No se encontraron libros para esa búsqueda.");
        }
    }
    private void listarAutoresVivosEnAnio(int anio) {
        // Obtener todos los autores de la base de datos
        Iterable<Author> autores = authorRepository.findAll();

        if (autores.iterator().hasNext()) {
            System.out.println("\n--- Autores Vivos en el Año " + anio + " ---");
            for (Author autor : autores) {
                // Verificar si el autor está vivo en el año proporcionado
                // Obtener el año de nacimiento y de fallecimiento, y convertirlos a Integer si no son nulos
                Integer anioNacimiento = (autor.getbirth_year() != null && !autor.getbirth_year().isEmpty())
                        ? Integer.valueOf(autor.getbirth_year())
                        : null;
                Integer anioFallecimiento = (autor.getdeath_year() != null && !autor.getdeath_year().isEmpty())
                        ? Integer.valueOf(autor.getdeath_year())
                        : null;

                // Verificar si el autor está vivo en el año proporcionado
                if (anioNacimiento != null && anioNacimiento <= anio &&
                        (anioFallecimiento == null || anioFallecimiento >= anio)) {
                    System.out.println("ID: " + autor.getId() + " | Nombre: " + autor.getName());
                }
            }
        } else {
            System.out.println("No hay autores registrados en la base de datos.");
        }
    }
    private void listarLibrosPorIdioma(String idioma) {

        Iterable<Book> libros = bookRepository.findAll();

        if (libros.iterator().hasNext()) {
            System.out.println("\n--- Libros en el Idioma " + idioma + " ---");
            for (Book libro : libros) {
                if (libro.getLanguages().contains(idioma)) {
                    System.out.println("ID: " + libro.getId() + " | Título: " + libro.getTitle());
                }
            }
        } else {
            System.out.println("No hay libros registrados en la base de datos.");
        }
    }
    private void listarAutoresRegistrados() {

        Iterable<Author> autores = authorRepository.findAll();

        if (autores.iterator().hasNext()) {
            System.out.println("\n--- Autores Registrados ---");
            for (Author autor : autores) {
                System.out.println("ID: " + autor.getId() + " | Nombre: " + autor.getName());
            }
        } else {
            System.out.println("No hay autores registrados en la base de datos.");
        }
    }
    private void listarLibrosRegistrados() {

        Iterable<Book> libros = bookRepository.findAll();

        if (libros.iterator().hasNext()) {
            System.out.println("\n--- Libros Registrados ---");
            for (Book libro : libros) {
                System.out.println("ID: " + libro.getId() + " | Título: " + libro.getTitle());
            }
        } else {
            System.out.println("No hay libros registrados en la base de datos.");
        }
    }
}

