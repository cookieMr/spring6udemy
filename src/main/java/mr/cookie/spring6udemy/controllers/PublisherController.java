package mr.cookie.spring6udemy.controllers;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.services.PublisherService;
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
@RequestMapping("publisher")
@RequiredArgsConstructor
public class PublisherController {

    @NotNull
    private final PublisherService publisherService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<Publisher> getAllPublishers() {
        return this.publisherService.findAll();
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Nullable
    public Publisher getPublisherById(@PathVariable Long id) {
        return this.publisherService.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @NotNull
    public Publisher createPublisher(@RequestBody Publisher publisher) {
        return this.publisherService.create(publisher);
        // TODO: conflict status
    }

    @PutMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @NotNull
    public Publisher updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        return this.publisherService.update(id, publisher);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublisher(@PathVariable Long id) {
        this.publisherService.deleteById(id);
    }

}
