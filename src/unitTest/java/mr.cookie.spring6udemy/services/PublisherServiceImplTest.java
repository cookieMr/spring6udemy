package mr.cookie.spring6udemy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    private static final UUID PUBLISHER_ID = UUID.randomUUID();
    private static final PublisherDto PUBLISHER_DTO = PublisherDto.builder()
            .id(PUBLISHER_ID)
            .build();
    private static final Supplier<PublisherEntity> PUBLISHER_DTO_SUPPLIER = () -> PublisherEntity.builder()
            .id(PUBLISHER_ID)
            .build();

    @Captor
    private ArgumentCaptor<PublisherEntity> publisherDtoArgumentCaptor;

    private PublisherMapper publisherMapper;
    private PublisherRepository publisherRepository;
    private PublisherServiceImpl publisherService;

    @BeforeEach
    void setupBeforeEach() {
        publisherRepository = mock(PublisherRepository.class);
        publisherMapper = spy(new PublisherMapperImpl());
        publisherService = new PublisherServiceImpl(25, publisherMapper, publisherRepository);
    }

    @AfterEach
    void cleanUp() {
        reset(publisherRepository, publisherMapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -42})
    void shouldFailInitializationWithInvalidPageSize(int invalidPageSize) {
        assertThatThrownBy(() -> new PublisherServiceImpl(invalidPageSize, publisherMapper, publisherRepository))
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The default pageSize should be greater than zero.");
    }

    @Test
    void shouldReturnAllPublishers() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var publisherPage = new PageImpl<>(List.of(publisherDto));

        when(publisherRepository.findAll(any(Pageable.class)))
                .thenReturn(publisherPage);

        var result = publisherService.findAll(null, null);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(publisherRepository).findAll(any(PageRequest.class));
        verify(publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldReturnPublisherById() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        when(publisherRepository.findById(any(UUID.class))).thenReturn(Optional.of(publisherDto));

        var result = publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(PUBLISHER_ID, PublisherDto::getId);

        verify(publisherRepository).findById(PUBLISHER_ID);
        verify(publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindPublisherById() {
        when(publisherRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

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
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(publisherDto);

        var result = publisherService.create(PUBLISHER_DTO);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, PublisherDto::getId);

        verify(publisherRepository).save(publisherDto);
        verify(publisherMapper).map(publisherDto);
        verify(publisherMapper).map(PUBLISHER_DTO);
        verifyNoMoreInteractions(publisherRepository, publisherMapper);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var updatedPublisher = PublisherDto.builder()
                .name("Penguin Random House")
                .address("Neumarkter Strasse 28")
                .state("Germany")
                .city("Munich")
                .zipCode("D-81673")
                .build();

        when(publisherRepository.findById(any(UUID.class))).thenReturn(Optional.of(publisherDto));
        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(publisherDto);

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
        verify(publisherMapper).map(publisherDto);
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
        when(publisherRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publisherService.update(PUBLISHER_ID, PUBLISHER_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        when(publisherRepository.existsById(any(UUID.class))).thenReturn(true);

        var result = publisherService.deleteById(PUBLISHER_ID);

        assertThat(result).isTrue();

        verify(publisherRepository).existsById(PUBLISHER_ID);
        verify(publisherRepository).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

    @Test
    void shouldNotDeleteNotExistingPublisher() {
        when(publisherRepository.existsById(any(UUID.class))).thenReturn(false);

        var result = publisherService.deleteById(PUBLISHER_ID);

        assertThat(result).isFalse();

        verify(publisherRepository).existsById(PUBLISHER_ID);
        verifyNoMoreInteractions(publisherRepository);
        verifyNoInteractions(publisherMapper);
    }

}
