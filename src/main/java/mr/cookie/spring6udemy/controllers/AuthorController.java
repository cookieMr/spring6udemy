package mr.cookie.spring6udemy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.AuthorService;
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
@RequestMapping(path = "/author")
@RequiredArgsConstructor
public class AuthorController {

    private static final String PATH_AUTHOR_ID_DESCRIPTION = "Author's ID";
    private static final String RESPONSE_400_DESCRIPTION = "Author has invalid values as fields.";
    private static final String RESPONSE_404_DESCRIPTION = "Author was not found by ID.";
    private static final String RESPONSE_409_DESCRIPTION = "Author with provided attributes already exists";

    @NotNull
    private final AuthorService authorService;

    @Operation(description = "Returns all authors (or an empty collection).")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @Operation(description = "Returns an author by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Author was found by ID."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION)})
    @GetMapping(path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Nullable
    public AuthorDto getAuthorById(@Parameter(description = PATH_AUTHOR_ID_DESCRIPTION) @PathVariable UUID id) {
        return authorService.findById(id);
    }

    @Operation(description = "Creates a new author and persists it.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Author was created and is returned in a response body."),
                    @ApiResponse(responseCode = "400", description = RESPONSE_400_DESCRIPTION),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@Validated @RequestBody AuthorDto author) {
        return authorService.create(author);
    }

    @Operation(description = "Updates an author by ID.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Author was updated and is returned in a response body."),
                    @ApiResponse(responseCode = "400", description = RESPONSE_400_DESCRIPTION),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)})
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public AuthorDto updateAuthor(
            @Parameter(description = PATH_AUTHOR_ID_DESCRIPTION) @PathVariable UUID id,
            @Validated @RequestBody AuthorDto author
    ) {
        return authorService.update(id, author);
    }

    @Operation(description = "Deletes an author by its ID.",
            responses = {@ApiResponse(responseCode = "204", description = "Author was found by ID and removed.")})
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@Parameter(description = PATH_AUTHOR_ID_DESCRIPTION) @PathVariable UUID id) {
        authorService.deleteById(id);
    }

}
