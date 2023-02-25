package mr.cookie.spring6udemy.controllers;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    @NotNull
    private final AuthorService authorService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<Author> getAllAuthors() {
        return this.authorService.findAll();
    }

}
