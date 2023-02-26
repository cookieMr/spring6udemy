package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.PublisherController;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.services.PublisherService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

    private static final long ID = 4L;
    private static final Publisher PUBLISHER = Publisher.builder().id(ID).build();

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

    @NotNull
    private static Stream<Publisher> publisherStream() {
        return Stream.of(PUBLISHER);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("publisherStream")
    void shouldGetPublisherById(@Nullable Publisher publisher) {
        when(this.publisherService.findById(anyLong())).thenReturn(publisher);

        var result = this.publisherController.getAuthorById(ID);

        assertThat(result)
                .isEqualTo(publisher);

        verify(this.publisherService).findById(ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldCreateNewPublisher() {
        when(this.publisherService.create(any(Publisher.class))).thenReturn(PUBLISHER);

        var result = this.publisherController.createNewPublisher(PUBLISHER);

        assertThat(result)
                .isNotNull()
                .isSameAs(PUBLISHER);

        verify(this.publisherService).create(PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

}
