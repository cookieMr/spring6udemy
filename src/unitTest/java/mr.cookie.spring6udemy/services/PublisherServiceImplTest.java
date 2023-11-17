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
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.mappers.PublisherMapperImpl;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    @Captor
    private ArgumentCaptor<PublisherEntity> captor;

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
    void shouldReturnPublisherById() {
        var publisherId = randomUUID();
        var publisherEntity = PublisherEntity.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findById(publisherId)).thenReturn(Optional.of(publisherEntity));

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

        when(repository.save(publisherEntity)).thenReturn(publisherEntity);

        var publisherDto = PublisherDto.builder()
                .id(publisherId)
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        var result = service.create(publisherDto);

        assertThat(result)
                .isNotNull()
                .returns(publisherId, PublisherDto::getId);

        verify(repository).save(publisherEntity);
        verify(mapper).map(publisherEntity);
        verify(mapper).map(publisherDto);
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
                .name(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();

        when(repository.findById(publisherId)).thenReturn(Optional.of(publisherEntity));
        when(repository.save(publisherEntity)).thenReturn(publisherEntity);

        var result = service.update(publisherId, updatedPublisherDto);

        assertThat(result)
                .isNotNull()
                .returns(publisherId, PublisherDto::getId)
                .returns(updatedPublisherDto.getName(), PublisherDto::getName)
                .returns(updatedPublisherDto.getAddress(), PublisherDto::getAddress)
                .returns(updatedPublisherDto.getState(), PublisherDto::getState)
                .returns(updatedPublisherDto.getCity(), PublisherDto::getCity)
                .returns(updatedPublisherDto.getZipCode(), PublisherDto::getZipCode);

        verify(repository).findById(publisherId);
        verify(repository).save(captor.capture());
        verify(mapper).map(publisherEntity);
        verifyNoMoreInteractions(repository, mapper);

        assertThat(captor.getValue())
                .isNotNull()
                .returns(publisherId, PublisherEntity::getId)
                .returns(updatedPublisherDto.getName(), PublisherEntity::getName)
                .returns(updatedPublisherDto.getAddress(), PublisherEntity::getAddress)
                .returns(updatedPublisherDto.getState(), PublisherEntity::getState)
                .returns(updatedPublisherDto.getCity(), PublisherEntity::getCity)
                .returns(updatedPublisherDto.getZipCode(), PublisherEntity::getZipCode);
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
    void shouldDeleteExistingPublisher() {
        var publisherId = randomUUID();

        service.deleteById(publisherId);

        verify(repository).deleteById(publisherId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
