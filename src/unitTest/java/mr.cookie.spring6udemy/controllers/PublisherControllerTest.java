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
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        var publisherDto = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

        when(service.findAll())
                .thenReturn(Stream.of(publisherDto));

        var result = controller.getAllPublishers();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(publisherDto)));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetPublisherById() {
        var publisherId = UUID.randomUUID();
        var publisherDto = PublisherDto.builder()
                .id(publisherId)
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        when(service.findById(publisherId)).thenReturn(Optional.of(publisherDto));

        var result = controller.getPublisherById(publisherId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(publisherDto));

        verify(service).findById(publisherId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var publisherId = UUID.randomUUID();
        when(service.findById(publisherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getPublisherById(publisherId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(service).findById(publisherId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewPublisher() {
        var publisherDto1st = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        var publisherDto2nd = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        when(service.create(publisherDto1st)).thenReturn(publisherDto2nd);

        var result = controller.createPublisher(publisherDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(publisherDto2nd));

        verify(service).create(publisherDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherId = UUID.randomUUID();
        var publisherDto1st = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        var publisherDto2nd = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        when(service.update(publisherId, publisherDto1st)).thenReturn(publisherDto2nd);

        var result = controller.updatePublisher(publisherId, publisherDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(publisherDto2nd));

        verify(service).update(publisherId, publisherDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var publisherId = UUID.randomUUID();
        var publisherDto = PublisherDto.builder()
                .id(UUID.randomUUID())
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        when(service.update(publisherId, publisherDto))
                .thenThrow(new NotFoundEntityException(publisherId, PublisherDto.class));

        assertThatThrownBy(() -> controller.updatePublisher(publisherId, publisherDto))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), publisherId);

        verify(service).update(publisherId, publisherDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        var publisherId = UUID.randomUUID();
        when(service.deleteById(publisherId)).thenReturn(true);

        var result = controller.deletePublisher(publisherId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.noContent().build());

        verify(service).deleteById(publisherId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        var publisherId = UUID.randomUUID();
        when(service.deleteById(publisherId)).thenReturn(false);

        assertThatThrownBy(() -> controller.deletePublisher(publisherId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, PublisherDto.class.getSimpleName(), publisherId);

        verify(service).deleteById(publisherId);
        verifyNoMoreInteractions(service);
    }

}
