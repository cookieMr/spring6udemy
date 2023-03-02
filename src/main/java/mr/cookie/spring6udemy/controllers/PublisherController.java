package mr.cookie.spring6udemy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
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

    private static final String PATH_PUBLISHER_ID_DESCRIPTION = "Publisher's ID";
    private static final String RESPONSE_404_DESCRIPTION = "Publisher was not found by ID.";
    private static final String RESPONSE_409_DESCRIPTION = "Publisher with provided attributes already exists";

    @NotNull
    private final PublisherService publisherService;

    @Operation(description = "Returns all publishers (or empty array).")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @NotNull
    public List<PublisherDto> getAllPublishers() {
        return this.publisherService.findAll();
    }

    @Operation(
            description = "Returns a publisher by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Publisher was found by ID."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION)
            }
    )
    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Nullable
    public PublisherDto getPublisherById(
            @Parameter(description = PATH_PUBLISHER_ID_DESCRIPTION) @PathVariable Long id
    ) {
        return this.publisherService.findById(id)
                .orElseThrow(NotFoundEntityException::new);
    }

    @Operation(
            description = "Creates a new publisher and persists it.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Publisher was created and is returned in a response body."),
                    @ApiResponse(responseCode = "409", description = RESPONSE_409_DESCRIPTION)
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @NotNull
    public PublisherDto createPublisher(@RequestBody PublisherDto publisher) {
        return this.publisherService.create(publisher);
        // TODO: conflict status
    }

    @Operation(
            description = "Updates a publisher by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Publisher was updated and is returned in a response body."),
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
    public PublisherDto updatePublisher(
            @Parameter(description = PATH_PUBLISHER_ID_DESCRIPTION) @PathVariable Long id,
            @RequestBody PublisherDto publisher
    ) {
        return this.publisherService.update(id, publisher);
    }

    @Operation(
            description = "Deletes a publisher by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Publisher was found by ID and removed."),
                    @ApiResponse(responseCode = "404", description = RESPONSE_404_DESCRIPTION)
            }
    )
    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublisher(
            @Parameter(description = PATH_PUBLISHER_ID_DESCRIPTION) @PathVariable Long id
    ) {
        this.publisherService.deleteById(id);
    }

}
