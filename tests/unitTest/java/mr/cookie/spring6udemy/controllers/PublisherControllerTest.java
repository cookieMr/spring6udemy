package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.PublisherDtoProvider.providePublisherDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.services.PublisherService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

    @Mock
    @NotNull
    private PublisherService service;

    @NotNull
    @InjectMocks
    private PublisherController controller;

    @Test
    void shouldGetAllPublishers() {
        var publisherDto = providePublisherDto(randomUUID());

        when(service.findAll())
                .thenReturn(List.of(publisherDto));

        var result = controller.getAllPublishers();

        assertThat(result)
                .isNotNull()
                .isEqualTo(List.of(publisherDto));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetPublisherById() {
        var publisherId = randomUUID();
        var publisherDto = providePublisherDto(publisherId);

        when(service.findById(publisherId))
                .thenReturn(publisherDto);

        var result = controller.getPublisherById(publisherId);

        assertThat(result).isSameAs(publisherDto);

        verify(service).findById(publisherId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var publisherId = randomUUID();

        when(service.findById(publisherId))
                .thenThrow(EntityNotFoundException.ofPublisher(publisherId));

        assertThatThrownBy(() -> controller.getPublisherById(publisherId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, PublisherEntity.class.getSimpleName(), publisherId);

        verify(service).findById(publisherId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewPublisher() {
        var publisherDto1st = providePublisherDto(randomUUID());
        var publisherDto2nd = providePublisherDto(randomUUID());

        when(service.create(publisherDto1st))
                .thenReturn(publisherDto2nd);

        var result = controller.createPublisher(publisherDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(publisherDto2nd);

        verify(service).create(publisherDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherId = randomUUID();
        var publisherDto1st = providePublisherDto(randomUUID());
        var publisherDto2nd = providePublisherDto(randomUUID());

        when(service.update(publisherId, publisherDto1st))
                .thenReturn(publisherDto2nd);

        var result = controller.updatePublisher(publisherId, publisherDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(publisherDto2nd);

        verify(service).update(publisherId, publisherDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdatePublisherById() {
        var publisherId = randomUUID();
        var publisherDto = providePublisherDto(publisherId);

        when(service.update(publisherId, publisherDto))
                .thenThrow(EntityNotFoundException.ofPublisher(publisherId));

        assertThatThrownBy(() -> controller.updatePublisher(publisherId, publisherDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, PublisherEntity.class.getSimpleName(), publisherId);

        verify(service).update(publisherId, publisherDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        var publisherId = randomUUID();

        controller.deletePublisher(publisherId);

        verify(service).deleteById(publisherId);
        verifyNoMoreInteractions(service);
    }

}
