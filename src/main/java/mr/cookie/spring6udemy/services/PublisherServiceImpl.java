package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    @NotNull
    private final PublisherMapper publisherMapper;

    @NotNull
    private final PublisherRepository publisherRepository;

    @NotNull
    @Override
    public List<PublisherDto> findAll() {
        return Optional.of(this.publisherRepository)
                .map(CrudRepository::findAll)
                .map(this.publisherMapper::mapToModel)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<PublisherDto> findById(@NotNull UUID id) {
        return this.publisherRepository.findById(id)
                .map(this.publisherMapper::map);
    }

    @Override
    public @NotNull PublisherDto create(@NotNull PublisherDto publisher) {
        return Optional.of(publisher)
                .map(this.publisherMapper::map)
                .map(this.publisherRepository::save)
                .map(this.publisherMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @Override
    public @NotNull PublisherDto update(@NotNull UUID id, @NotNull PublisherDto publisher) {
        var existingDto = this.publisherRepository.findById(id)
                .orElseThrow(NotFoundEntityException::new);

        existingDto.setName(publisher.getName());
        existingDto.setAddress(publisher.getAddress());
        existingDto.setCity(publisher.getCity());
        existingDto.setState(publisher.getState());
        existingDto.setZipCode(publisher.getZipCode());

        return Optional.of(existingDto)
                .map(this.publisherRepository::save)
                .map(this.publisherMapper::map)
                .orElseThrow();
    }

    @Override
    public boolean deleteById(@NotNull UUID id) {
        var doesBeerExist = this.publisherRepository.existsById(id);

        if (doesBeerExist) {
            this.publisherRepository.deleteById(id);
        }

        return doesBeerExist;
    }

}
