package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.PublisherDto;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.mappers.PublisherMapperImpl;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import mr.cookie.spring6udemy.services.PublisherServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    private static final long PUBLISHER_ID = 5L;
    private static final PublisherDto PUBLISHER_DTO = PublisherDto.builder().id(PUBLISHER_ID).build();
    private static final Publisher PUBLISHER = Publisher.builder().id(PUBLISHER_ID).build();

    @Spy
    @NotNull
    private PublisherMapper publisherMapper = new PublisherMapperImpl();

    @Mock
    @NotNull
    private PublisherRepository publisherRepository;

    @NotNull
    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Test
    void shouldFindAllPublishers() {
        when(this.publisherRepository.findAll()).thenReturn(Collections.singletonList(PUBLISHER_DTO));

        var result = this.publisherService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnly(Publisher.builder().id(PUBLISHER_ID).build());

        verify(this.publisherRepository).findAll();
        verify(this.publisherMapper).mapToModel(anyIterable());
        verify(this.publisherMapper).map(PUBLISHER_DTO);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldFindPublisherById() {
        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.of(PUBLISHER_DTO));

        var result = this.publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verify(this.publisherMapper).map(PUBLISHER_DTO);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldCreateNewBook() {
        when(this.publisherRepository.save(any(PublisherDto.class))).thenReturn(PUBLISHER_DTO);

        var result = this.publisherService.create(PUBLISHER);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId);

        verify(this.publisherRepository).save(PUBLISHER_DTO);
        verify(this.publisherMapper).map(PUBLISHER_DTO);
        verify(this.publisherMapper).map(PUBLISHER);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

}
