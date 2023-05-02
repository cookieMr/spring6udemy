package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final int defaultPageSize;

    @NotNull
    private final PublisherMapper publisherMapper;

    @NotNull
    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(
            @Value("${app.pagination.default-page-size:25}") int defaultPageSize,
            @NotNull PublisherMapper publisherMapper,
            @NotNull PublisherRepository publisherRepository
    ) {
        this.defaultPageSize = validateDefaultPageSize(defaultPageSize);
        this.publisherMapper = publisherMapper;
        this.publisherRepository = publisherRepository;
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public Page<PublisherDto> findAll(@Nullable Integer pageNumber, @Nullable Integer pageSize) {
        var pageRequest = createPageRequest(pageNumber, pageSize, this.defaultPageSize);
        return this.publisherRepository.findAll(pageRequest)
                .map(this.publisherMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublisherDto> findById(@NotNull UUID id) {
        return this.publisherRepository.findById(id)
                .map(this.publisherMapper::map);
    }

    @Override
    @Transactional
    public @NotNull PublisherDto create(@NotNull PublisherDto publisher) {
        return Optional.of(publisher)
                .map(this.publisherMapper::map)
                .map(this.publisherRepository::save)
                .map(this.publisherMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @Override
    @Transactional
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
    @Transactional
    public boolean deleteById(@NotNull UUID id) {
        var doesBeerExist = this.publisherRepository.existsById(id);

        if (doesBeerExist) {
            this.publisherRepository.deleteById(id);
        }

        return doesBeerExist;
    }

}
