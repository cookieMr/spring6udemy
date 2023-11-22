package mr.cookie.spring6udemy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/book")
@RequiredArgsConstructor
public class BookController {

    private static final String PATH_BOOK_ID_DESCRIPTION = "Book's ID";
    private static final String RESPONSE_400_DESCRIPTION = "Book has invalid values as fields.";
    private static final String RESPONSE_404_DESCRIPTION = "Book was not found by ID.";
    private static final String RESPONSE_409_DESCRIPTION = "Book with provided attributes already exists";

    @NotNull
    private final BookService bookService;

    @Operation(description = "Returns all books (or an empty collection).")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @Operation(description = "Returns all books (or an empty collection).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Author was found by ID."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION)})
    @GetMapping(path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Nullable
    public BookDto getBookById(@Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable UUID id) {
        return bookService.findById(id);
    }

    @Operation(description = "Creates a new book and persists it.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Book was created and is returned in a response body."),
                    @ApiResponse(responseCode = "400", description = RESPONSE_400_DESCRIPTION),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Validated @RequestBody BookDto book) {
        return bookService.create(book);
    }

    @Operation(description = "Updates a book by ID.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Book was updated and is returned in a response body."),
                    @ApiResponse(responseCode = "400", description = RESPONSE_400_DESCRIPTION),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)})
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public BookDto updateBook(
            @Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable UUID id,
            @Validated @RequestBody BookDto book
    ) {
        return bookService.update(id, book);
    }

    @Operation(description = "Deletes a book by its ID.",
            responses = {@ApiResponse(responseCode = "204", description = "Book was found by ID and removed.")})
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@Parameter(description = PATH_BOOK_ID_DESCRIPTION) @PathVariable UUID id) {
        bookService.deleteById(id);
    }

}
