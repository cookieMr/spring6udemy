package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.PublisherController;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.services.PublisherService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherControllerTest {

    @Mock
    @NotNull
    private PublisherService publisherService;

    @NotNull
    @InjectMocks
    private PublisherController publisherController;

    @Test
    void shouldGetAllPublishers() {
        var publisher = Publisher.builder().id(4L).build();

        when(this.publisherService.findAll()).thenReturn(Collections.singletonList(publisher));

        var result = this.publisherController.getAllPublishers();

        assertThat(result).containsOnly(publisher);

        verify(this.publisherService).findAll();
        verifyNoMoreInteractions(this.publisherService);
    }

}
