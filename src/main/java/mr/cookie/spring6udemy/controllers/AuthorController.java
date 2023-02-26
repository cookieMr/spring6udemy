package mr.cookie.spring6udemy.controllers;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("author")
@RequiredArgsConstructor
public class AuthorController {

    @NotNull
    private final AuthorService authorService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<Author> getAllAuthors() {
        return this.authorService.findAll();
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Nullable
    public Author getAuthorById(@PathVariable Long id) {
        return this.authorService.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @NotNull
    public Author createNewAuthor(@RequestBody Author author) {
        return this.authorService.create(author);
        // TODO: 201 status & conflict status
    }

}
