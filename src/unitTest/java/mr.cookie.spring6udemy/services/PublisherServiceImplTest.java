package mr.cookie.spring6udemy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
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

    private static final UUID PUBLISHER_ID = UUID.randomUUID();
    private static final PublisherDto PUBLISHER_DTO = PublisherDto.builder()
            .id(PUBLISHER_ID)
            .build();
    private static final Supplier<PublisherEntity> PUBLISHER_ENTITY_SUPPLIER = () -> PublisherEntity.builder()
            .id(PUBLISHER_ID)
            .build();

    @Captor
    private ArgumentCaptor<PublisherEntity> publisherDtoArgumentCaptor;

    @Spy
    private PublisherMapper publisherMapper = new PublisherMapperImpl();

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Test
    void shouldReturnAllPublishers() {
        var publisherEntity = PUBLISHER_ENTITY_SUPPLIER.get();

        when(publisherRepository.findAll())
                .thenReturn(List.of(publisherEntity));

        var result = publisherService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(publisherRepository).findAll();
        verify(publisherMapper).map(publisherEntity);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldReturnPublisherById() {
        var publisherEntity = PUBLISHER_ENTITY_SUPPLIER.get();

        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.of(publisherEntity));

        var result = publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(PUBLISHER_ID, PublisherDto::getId);

        verify(publisherRepository).findById(PUBLISHER_ID);
        verify(publisherMapper).map(publisherEntity);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindPublisherById() {
        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.empty());

        var result = publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldCreateNewPublisher() {
        var publisherEntity = PUBLISHER_ENTITY_SUPPLIER.get();

        when(publisherRepository.save(publisherEntity)).thenReturn(publisherEntity);

        var result = publisherService.create(PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, PublisherDto::getId);

        verify(publisherRepository).save(publisherEntity);
        verify(publisherMapper).map(publisherEntity);
        verify(publisherMapper).map(PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherEntity = PUBLISHER_ENTITY_SUPPLIER.get();
        var updatedPublisher = PublisherDto.builder()
                .name("Penguin Random House")
                .address("Neumarkter Strasse 28")
                .state("Germany")
                .city("Munich")
                .zipCode("D-81673")
                .build();

        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.of(publisherEntity));
        when(publisherRepository.save(publisherEntity)).thenReturn(publisherEntity);

        var result = publisherService.update(PUBLISHER_ID, updatedPublisher);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, PublisherDto::getId)
                .returns(updatedPublisher.getName(), PublisherDto::getName)
                .returns(updatedPublisher.getAddress(), PublisherDto::getAddress)
                .returns(updatedPublisher.getState(), PublisherDto::getState)
                .returns(updatedPublisher.getCity(), PublisherDto::getCity)
                .returns(updatedPublisher.getZipCode(), PublisherDto::getZipCode);

        verify(publisherRepository).findById(PUBLISHER_ID);
        verify(publisherRepository).save(publisherDtoArgumentCaptor.capture());
        verify(publisherMapper).map(publisherEntity);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);

        assertThat(publisherDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(PUBLISHER_ID, PublisherEntity::getId)
                .returns(updatedPublisher.getName(), PublisherEntity::getName)
                .returns(updatedPublisher.getAddress(), PublisherEntity::getAddress)
                .returns(updatedPublisher.getState(), PublisherEntity::getState)
                .returns(updatedPublisher.getCity(), PublisherEntity::getCity)
                .returns(updatedPublisher.getZipCode(), PublisherEntity::getZipCode);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdatePublisherById() {
        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publisherService.update(PUBLISHER_ID, PUBLISHER_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        when(publisherRepository.existsById(PUBLISHER_ID)).thenReturn(true);

        var result = publisherService.deleteById(PUBLISHER_ID);

        assertThat(result).isTrue();

        verify(publisherRepository).existsById(PUBLISHER_ID);
        verify(publisherRepository).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldNotDeleteNotExistingPublisher() {
        when(publisherRepository.existsById(PUBLISHER_ID)).thenReturn(false);

        var result = publisherService.deleteById(PUBLISHER_ID);

        assertThat(result).isFalse();

        verify(publisherRepository).existsById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

}
