package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    @NotNull
    private final AuthorMapper authorMapper;

    @NotNull
    private final AuthorRepository authorRepository;

    @NotNull
    @Override
    public List<AuthorDto> findAll() {
        return Optional.of(this.authorRepository)
                .map(CrudRepository::findAll)
                .map(this.authorMapper::mapToModel)
                .orElse(Collections.emptyList());
    }

    @Override
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
