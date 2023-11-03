package mr.cookie.spring6udemy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.services.PublisherService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

    private static final UUID PUBLISHER_ID = UUID.randomUUID();
    private static final PublisherDto PUBLISHER_DTO = PublisherDto.builder().id(PUBLISHER_ID).build();

    @Mock
    @NotNull
    private PublisherService publisherService;

    @NotNull
    @InjectMocks
    private PublisherController publisherController;

    @Test
    void shouldGetAllPublishers() {
        when(publisherService.findAll())
                .thenReturn(Stream.of(PUBLISHER_DTO));

        var result = publisherController.getAllPublishers();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(PUBLISHER_DTO)));

        verify(publisherService).findAll();
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldGetPublisherById() {
        when(publisherService.findById(PUBLISHER_ID)).thenReturn(Optional.of(PUBLISHER_DTO));

        var result = publisherController.getPublisherById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER_DTO);

        verify(publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(publisherService.findById(PUBLISHER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publisherController.getPublisherById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldCreateNewPublisher() {
        when(publisherService.create(PUBLISHER_DTO)).thenReturn(PUBLISHER_DTO);

        var result = publisherController.createPublisher(PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(publisherService).create(PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        when(publisherService.update(PUBLISHER_ID, PUBLISHER_DTO)).thenReturn(PUBLISHER_DTO);

        var result = publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(publisherService).update(PUBLISHER_ID, PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(publisherService.update(PUBLISHER_ID, PUBLISHER_DTO))
                .thenThrow(new NotFoundEntityException(PUBLISHER_ID, PublisherDto.class));

        assertThatThrownBy(() -> publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), PUBLISHER_ID);

        verify(publisherService).update(PUBLISHER_ID, PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        when(publisherService.deleteById(PUBLISHER_ID)).thenReturn(true);

        publisherController.deletePublisher(PUBLISHER_ID);

        verify(publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(publisherService.deleteById(PUBLISHER_ID)).thenReturn(false);

        assertThatThrownBy(() -> publisherController.deletePublisher(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), PUBLISHER_ID);

        verify(publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

}
