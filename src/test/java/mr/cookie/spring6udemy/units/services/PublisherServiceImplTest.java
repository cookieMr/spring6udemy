package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.PublisherDto;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.mappers.PublisherMapperImpl;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import mr.cookie.spring6udemy.services.PublisherServiceImpl;
import mr.cookie.spring6udemy.services.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    private static final long PUBLISHER_ID = 5L;
    private static final Publisher PUBLISHER = Publisher.builder().id(PUBLISHER_ID).build();
    private static final Supplier<PublisherDto> PUBLISHER_DTO_SUPPLIER = () -> PublisherDto.builder().id(PUBLISHER_ID).build();

    @Spy
    @NotNull
    private PublisherMapper publisherMapper = new PublisherMapperImpl();

    @Mock
    @NotNull
    private PublisherRepository publisherRepository;

    @NotNull
    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Captor
    private ArgumentCaptor<PublisherDto> publisherDtoArgumentCaptor;

    @Test
    void shouldReturnAllPublishers() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        when(this.publisherRepository.findAll()).thenReturn(Collections.singletonList(publisherDto));

        var result = this.publisherService.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(Publisher.builder().id(PUBLISHER_ID).build());

        verify(this.publisherRepository).findAll();
        verify(this.publisherMapper).mapToModel(anyIterable());
        verify(this.publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldReturnPublisherById() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.of(publisherDto));

        var result = this.publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verify(this.publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindPublisherById() {
        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.publisherService.findById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Publisher.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherRepository);
        verifyNoInteractions(this.publisherMapper);
    }

    @Test
    void shouldCreateNewPublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        when(this.publisherRepository.save(any(PublisherDto.class))).thenReturn(publisherDto);

        var result = this.publisherService.create(PUBLISHER);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId);

        verify(this.publisherRepository).save(publisherDto);
        verify(this.publisherMapper).map(publisherDto);
        verify(this.publisherMapper).map(PUBLISHER);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldUpdateExistingPublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var updatedPublisher = Publisher.builder()
                .name("Penguin Random House")
                .address("Neumarkter Strasse 28")
                .state("Germany")
                .city("Munich")
                .zipCode("D-81673")
                .build();

        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.of(publisherDto));
        when(this.publisherRepository.save(any(PublisherDto.class))).thenReturn(publisherDto);

        var result = this.publisherService.update(PUBLISHER_ID, updatedPublisher);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId)
                .returns(updatedPublisher.getName(), Publisher::getName)
                .returns(updatedPublisher.getAddress(), Publisher::getAddress)
                .returns(updatedPublisher.getState(), Publisher::getState)
                .returns(updatedPublisher.getCity(), Publisher::getCity)
                .returns(updatedPublisher.getZipCode(), Publisher::getZipCode);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verify(this.publisherRepository).save(this.publisherDtoArgumentCaptor.capture());
        verify(this.publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);

        assertThat(this.publisherDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(PUBLISHER_ID, PublisherDto::getId)
                .returns(updatedPublisher.getName(), PublisherDto::getName)
                .returns(updatedPublisher.getAddress(), PublisherDto::getAddress)
                .returns(updatedPublisher.getState(), PublisherDto::getState)
                .returns(updatedPublisher.getCity(), PublisherDto::getCity)
                .returns(updatedPublisher.getZipCode(), PublisherDto::getZipCode);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdatePublisherById() {
        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.publisherService.update(PUBLISHER_ID, PUBLISHER))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Publisher.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherRepository);
        verifyNoInteractions(this.publisherMapper);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.of(PUBLISHER_DTO_SUPPLIER.get()));

        this.publisherService.deleteById(PUBLISHER_ID);

        verify(this.publisherRepository).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherRepository);
        verifyNoInteractions(this.publisherMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeletePublisherById() {
        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.publisherService.deleteById(PUBLISHER_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Publisher.class.getSimpleName(), PUBLISHER_ID);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherRepository);
        verifyNoInteractions(this.publisherMapper);
    }

}
