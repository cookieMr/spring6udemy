package mr.cookie.spring6udemy.controllers;

import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.services.PublisherService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        var pagePublisher = new PageImpl<>(List.of(PUBLISHER_DTO));

        when(this.publisherService.findAll(null, null))
                .thenReturn(pagePublisher);

        var result = this.publisherController.getAllPublishers(null, null);

        assertThat(result)
                .isSameAs(pagePublisher);

        verify(this.publisherService).findAll(null, null);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGetPublisherById() {
        when(this.publisherService.findById(any(UUID.class))).thenReturn(Optional.of(PUBLISHER_DTO));

        var result = this.publisherController.getPublisherById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER_DTO);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.publisherService.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.publisherController.getPublisherById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldCreateNewPublisher() {
        when(this.publisherService.create(any(PublisherDto.class))).thenReturn(PUBLISHER_DTO);

        var result = this.publisherController.createPublisher(PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(this.publisherService).create(PUBLISHER_DTO);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        when(this.publisherService.update(any(UUID.class), any(PublisherDto.class))).thenReturn(PUBLISHER_DTO);

        var result = this.publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER_DTO);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.publisherService.update(any(UUID.class), any(PublisherDto.class)))
                .thenThrow(new NotFoundEntityException(PUBLISHER_ID, PublisherDto.class));

        assertThatThrownBy(() -> this.publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER_DTO);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        when(this.publisherService.deleteById(any(UUID.class))).thenReturn(true);

        this.publisherController.deletePublisher(PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(this.publisherService.deleteById(any(UUID.class))).thenReturn(false);

        assertThatThrownBy(() -> this.publisherController.deletePublisher(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

}
