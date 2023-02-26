package mr.cookie.spring6udemy.controllers;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {

    @NotNull
    private final BookService bookService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<Book> getAllBooks() {
        return this.bookService.findAll();
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Nullable
    public Book getAuthorById(@PathVariable Long id) {
        return this.bookService.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @NotNull
    public Book createNewBook(@RequestBody Book book) {
        return this.bookService.create(book);
        // TODO: 201 status & conflict status
    }

}
