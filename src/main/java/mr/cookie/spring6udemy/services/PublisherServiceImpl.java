package mr.cookie.spring6udemy.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
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
    public List<PublisherDto> findAll() {
        return publisherRepository.findAll()
                .stream()
                .map(publisherMapper::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PublisherDto findById(@NotNull UUID id) {
        return publisherRepository.findById(id)
                .map(publisherMapper::map)
                .orElseThrow(() -> EntityNotFoundException.ofPublisher(id));
    }

    @NotNull
    @Override
    @Transactional
    public PublisherDto create(@NotNull PublisherDto publisher) {
        var doesPublisherExist = publisherRepository.findByName(publisher.getName())
                .isPresent();
        if (doesPublisherExist) {
            throw EntityExistsException.ofPublisher();
        }

        return Optional.of(publisher)
                .map(publisherMapper::map)
                .map(publisherRepository::save)
                .map(publisherMapper::map)
                .orElseThrow();
    }

    @NotNull
    @Override
    @Transactional
    public PublisherDto update(@NotNull UUID id, @NotNull PublisherDto publisher) {
        var existingDto = publisherRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.ofPublisher(id));

        if (publisherRepository.findByName(publisher.getName()).isPresent()) {
            throw EntityExistsException.ofPublisher();
        }

        existingDto.setName(publisher.getName())
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
    public void deleteById(@NotNull UUID id) {
        publisherRepository.deleteById(id);
    }

}
