package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
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
public class AuthorServiceImpl implements AuthorService {

    private final int defaultPageSize;

    @NotNull
    private final AuthorMapper authorMapper;

    @NotNull
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(
            @Value("${app.pagination.default-page-size:25}") int defaultPageSize,
            @NotNull AuthorMapper authorMapper,
            @NotNull AuthorRepository authorRepository) {
        this.defaultPageSize = validateDefaultPageSize(defaultPageSize);
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public Page<AuthorDto> findAll(@Nullable Integer pageNumber, @Nullable Integer pageSize) {
        var pageRequest = createPageRequest(pageNumber, pageSize, this.defaultPageSize);
        return this.authorRepository.findAll(pageRequest)
                .map(this.authorMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorDto> findById(@NotNull UUID id) {
        return this.authorRepository.findById(id)
                .map(this.authorMapper::map);
    }

    @NotNull
    @Override
    @Transactional
    public AuthorDto create(@NotNull AuthorDto author) {
        return Optional.of(author)
                .map(this.authorMapper::map)
                .map(this.authorRepository::save)
                .map(this.authorMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    @Transactional
    public AuthorDto update(@NotNull UUID id, @NotNull AuthorDto author) {
        var existingDto = this.authorRepository.findById(id)
                .orElseThrow(NotFoundEntityException::new);

        existingDto.setFirstName(author.getFirstName());
        existingDto.setLastName(author.getLastName());

        return Optional.of(existingDto)
                .map(this.authorRepository::save)
                .map(this.authorMapper::map)
                .orElseThrow();
    }

    @Override
    @Transactional
    public boolean deleteById(@NotNull UUID id) {
        var doesAuthorExist = this.authorRepository.existsById(id);

        if (doesAuthorExist) {
            this.authorRepository.deleteById(id);
        }

        return doesAuthorExist;
    }

}
