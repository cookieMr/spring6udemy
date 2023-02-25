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
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    private static final long PUBLISHER_ID = 5L;

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
        var publisherDto = PublisherDto.builder().id(PUBLISHER_ID).build();

        when(this.publisherRepository.findAll())
                .thenReturn(Collections.singletonList(publisherDto));

        var result = this.publisherService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnly(Publisher.builder().id(PUBLISHER_ID).build());

        verify(this.publisherRepository).findAll();
        verify(this.publisherMapper).mapToModel(anyIterable());
        verify(this.publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

    @Test
    void shouldFindPublisherById() {
        var publisherDto = PublisherDto.builder().id(PUBLISHER_ID).build();

        when(this.publisherRepository.findById(anyLong())).thenReturn(Optional.of(publisherDto));

        var result = this.publisherService.findById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .returns(PUBLISHER_ID, Publisher::getId);

        verify(this.publisherRepository).findById(PUBLISHER_ID);
        verify(this.publisherMapper).map(publisherDto);
        verifyNoMoreInteractions(this.publisherRepository, this.publisherMapper);
    }

}
