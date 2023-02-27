package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.PublisherController;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.services.PublisherService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

    private static final long PUBLISHER_ID = 4L;
    private static final Publisher PUBLISHER = Publisher.builder().id(PUBLISHER_ID).build();

    @Mock
    @NotNull
    private PublisherService publisherService;

    @NotNull
    @InjectMocks
    private PublisherController publisherController;

    @Test
    void shouldGetAllPublishers() {
        when(this.publisherService.findAll()).thenReturn(Collections.singletonList(PUBLISHER));

        var result = this.publisherController.getAllPublishers();

        assertThat(result)
                .isNotNull()
                .containsOnly(PUBLISHER);

        verify(this.publisherService).findAll();
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGetPublisherById() {
        when(this.publisherService.findById(anyLong())).thenReturn(Optional.of(PUBLISHER));

        var result = this.publisherController.getPublisherById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.publisherService.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.publisherController.getPublisherById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldCreateNewPublisher() {
        when(this.publisherService.create(any(Publisher.class))).thenReturn(PUBLISHER);

        var result = this.publisherController.createPublisher(PUBLISHER);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER);

        verify(this.publisherService).create(PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        when(this.publisherService.update(anyLong(), any(Publisher.class))).thenReturn(PUBLISHER);

        var result = this.publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.publisherService.update(anyLong(), any(Publisher.class)))
                .thenThrow(new NotFoundEntityException(PUBLISHER_ID, Publisher.class));

        assertThatThrownBy(() -> this.publisherController.updatePublisher(PUBLISHER_ID, PUBLISHER))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Publisher.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        this.publisherController.deletePublisher(PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        doThrow(new NotFoundEntityException(PUBLISHER_ID, Publisher.class))
                .when(this.publisherService).deleteById(anyLong());

        assertThatThrownBy(() -> this.publisherController.deletePublisher(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Publisher.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

}
