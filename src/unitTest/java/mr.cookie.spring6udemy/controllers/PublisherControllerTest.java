package mr.cookie.spring6udemy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.services.PublisherService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

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

        when(publisherService.findAll(null, null))
                .thenReturn(pagePublisher);

        var result = publisherController.getAllPublishers(null, null);

        assertThat(result)
                .isSameAs(pagePublisher);

        verify(publisherService).findAll(null, null);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldGetPublisherById() {
        when(publisherService.findById(any(UUID.class))).thenReturn(Optional.of(PUBLISHER_DTO));

        var result = publisherController.getPublisherById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER_DTO);

        verify(publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(publisherService.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publisherController.getPublisherById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldCreateNewPublisher() {
        when(publisherService.create(any(PublisherDto.class))).thenReturn(PUBLISHER_DTO);

        var result = publisherController.createPublisher(PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(publisherService).create(PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        when(publisherService.update(any(UUID.class), any(PublisherDto.class))).thenReturn(PUBLISHER_DTO);

        var result = publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER_DTO);

        verify(publisherService).update(PUBLISHER_ID, PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(publisherService.update(any(UUID.class), any(PublisherDto.class)))
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
        when(publisherService.deleteById(any(UUID.class))).thenReturn(true);

        publisherController.deletePublisher(PUBLISHER_ID);

        verify(publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(publisherService.deleteById(any(UUID.class))).thenReturn(false);

        assertThatThrownBy(() -> publisherController.deletePublisher(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), PUBLISHER_ID);

        verify(publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherService);
    }

}
