package mr.cookie.spring6udemy.services;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    @NotNull
    private final PublisherMapper publisherMapper;

    @NotNull
    private final PublisherRepository publisherRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public Stream<PublisherDto> findAll() {
        return publisherRepository.findAll()
                .stream()
                .map(publisherMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublisherDto> findById(@NotNull UUID id) {
        return publisherRepository.findById(id)
                .map(publisherMapper::map);
    }

    @Override
    @Transactional
    public @NotNull PublisherDto create(@NotNull PublisherDto publisher) {
        return Optional.of(publisher)
                .map(publisherMapper::map)
                .map(publisherRepository::save)
                .map(publisherMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @Override
    @Transactional
    public @NotNull PublisherDto update(@NotNull UUID id, @NotNull PublisherDto publisher) {
        var existingDto = publisherRepository.findById(id)
                .orElseThrow(NotFoundEntityException::new)
                .setName(publisher.getName())
                .setAddress(publisher.getAddress())
                .setCity(publisher.getCity())
                .setState(publisher.getState())
                .setZipCode(publisher.getZipCode());

        return Optional.of(existingDto)
                .map(publisherRepository::save)
                .map(publisherMapper::map)
                .orElseThrow();
    }

    @Override
    @Transactional
    public boolean deleteById(@NotNull UUID id) {
        var doesBeerExist = publisherRepository.existsById(id);

        if (doesBeerExist) {
            publisherRepository.deleteById(id);
        }

        return doesBeerExist;
    }

}
