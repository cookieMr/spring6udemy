package mr.cookie.spring6udemy.services;

import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var pageRequest = createPageRequest(pageNumber, pageSize, defaultPageSize);
        return publisherRepository.findAll(pageRequest)
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
                .orElseThrow(NotFoundEntityException::new);

        existingDto.setName(publisher.getName());
        existingDto.setAddress(publisher.getAddress());
        existingDto.setCity(publisher.getCity());
        existingDto.setState(publisher.getState());
        existingDto.setZipCode(publisher.getZipCode());

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
