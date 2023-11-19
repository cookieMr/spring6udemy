package mr.cookie.spring6udemy.services;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.mappers.PublisherMapperImpl;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    @Spy
    private PublisherMapper mapper = new PublisherMapperImpl();

    @Mock
    private PublisherRepository repository;

    @InjectMocks
    private PublisherServiceImpl service;

    @Test
    void shouldReturnAllPublishers() {
        var publisherEntity = PublisherEntity.builder()
                .id(randomUUID())
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findAll())
                .thenReturn(List.of(publisherEntity));

        var result = service.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(repository).findAll();
        verify(mapper).map(publisherEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldFindPublisherById() {
        var publisherId = randomUUID();
        var publisherEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findById(publisherId))
                .thenReturn(Optional.of(publisherEntity));

        var result = service.findById(publisherId);

        assertThat(result)
                .isNotNull()
                .returns(publisherId, PublisherDto::getId);

        verify(repository).findById(publisherId);
        verify(mapper).map(publisherEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindPublisherById() {
        var publisherId = randomUUID();
        when(repository.findById(publisherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(publisherId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, PublisherEntity.class.getSimpleName(), publisherId);

        verify(repository).findById(publisherId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldCreateNewPublisher() {
        var publisherId = randomUUID();
        var publisherEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        var publisherDto = PublisherDto.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findByName(publisherDto.getName()))
                .thenReturn(Optional.empty());
        when(repository.save(publisherEntity)).thenReturn(publisherEntity);

        var result = service.create(publisherDto);

        assertThat(result)
                .isNotNull()
                .returns(publisherId, PublisherDto::getId)
                .returns(publisherEntity.getName(), PublisherDto::getName)
                .returns(publisherEntity.getAddress(), PublisherDto::getAddress)
                .returns(publisherEntity.getCity(), PublisherDto::getCity)
                .returns(publisherEntity.getState(), PublisherDto::getState)
                .returns(publisherEntity.getZipCode(), PublisherDto::getZipCode);

        verify(repository).findByName(publisherDto.getName());
        verify(repository).save(publisherEntity);
        verify(mapper).map(publisherEntity);
        verify(mapper).map(publisherDto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturnExistingEntityWhenCreatingSamePublisher() {
        var publisherId = randomUUID();
        var publisherEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        var publisherDto = PublisherDto.builder()
                .id(publisherId)
                .name(publisherEntity.getName())
                .city(publisherEntity.getCity())
                .address(publisherEntity.getAddress())
                .state(publisherEntity.getState())
                .zipCode(publisherEntity.getZipCode())
                .build();

        when(repository.findByName(publisherDto.getName()))
                .thenReturn(Optional.of(publisherEntity));

        var result = service.create(publisherDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(publisherDto);

        verify(repository).findByName(publisherDto.getName());
        verify(mapper).map(publisherEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherId = randomUUID();
        var publisherEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        var updatedPublisherDto = PublisherDto.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        var updatedEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findByName(updatedPublisherDto.getName()))
                .thenReturn(Optional.empty());
        when(repository.findById(publisherId)).thenReturn(Optional.of(publisherEntity));
        when(repository.save(publisherEntity)).thenReturn(publisherEntity);

        var result = service.update(publisherId, updatedPublisherDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(updatedPublisherDto);

        verify(repository).findByName(updatedPublisherDto.getName());
        verify(repository).findById(publisherId);
        verify(repository).save(updatedEntity);
        verify(mapper).map(publisherEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdatePublisherById() {
        var publisherId = randomUUID();
        when(repository.findById(publisherId)).thenReturn(Optional.empty());

        var publisherDto = PublisherDto.builder()
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        assertThatThrownBy(() -> service.update(publisherId, publisherDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, PublisherEntity.class.getSimpleName(), publisherId);

        verify(repository).findById(publisherId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenSamePublisherAlreadyExists() {
        var publisherId = randomUUID();
        var publisherDto = PublisherDto.builder()
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        var publisherEntity = PublisherEntity.builder()
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findById(publisherId))
                .thenReturn(Optional.of(publisherEntity));
        when(repository.findByName(publisherDto.getName()))
                .thenReturn(Optional.of(publisherEntity));

        assertThatThrownBy(() -> service.update(publisherId, publisherDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityExistsException.class)
                .hasMessage(EntityExistsException.ERROR_MESSAGE, PublisherEntity.class.getSimpleName());

        verify(repository).findById(publisherId);
        verify(repository).findByName(publisherDto.getName());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        var publisherId = randomUUID();

        service.deleteById(publisherId);

        verify(repository).deleteById(publisherId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
