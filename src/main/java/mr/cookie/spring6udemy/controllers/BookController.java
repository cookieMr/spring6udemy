package mr.cookie.spring6udemy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.model.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {

    private static final String PATH_BOOK_ID_DESCRIPTION = "Book's ID";
    private static final String RESPONSE_404_DESCRIPTION = "Book was not found by ID.";
    private static final String RESPONSE_409_DESCRIPTION = "Book with provided attributes already exists";

    @NotNull
    private final BookService bookService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<BookDto> getAllBooks() {
        return this.bookService.findAll();
    }

    @Operation(description = "Returns all books (or empty array).")
    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Nullable
    public BookDto getBookById(
            @Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable Long id
    ) {
        return this.bookService.findById(id)
                .orElseThrow(NotFoundEntityException::new);
    }

    @Operation(
            description = "Creates a new book and persists it.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Book was created and is returned in a response body."),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @NotNull
    public BookDto createBook(
            @Parameter(description = PATH_BOOK_ID_DESCRIPTION) @RequestBody BookDto book
    ) {
        return this.bookService.create(book);
        // TODO: conflict status
    }

    @Operation(
            description = "Updates a book by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book was updated and is returned in a response body."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)
            }
    )
    @PutMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @NotNull
    public BookDto updateBook(
            @Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable Long id,
            @RequestBody BookDto book
    ) {
        return this.bookService.update(id, book);
    }

    @Operation(
            description = "Deletes a book by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book was found by ID and removed."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION)
            }
    )
    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable Long id
    ) {
        this.bookService.deleteById(id);
    }

}
