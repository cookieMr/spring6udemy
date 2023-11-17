package mr.cookie.spring6udemy.services;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    @NotNull
    private final AuthorMapper authorMapper;

    @NotNull
    private final AuthorRepository authorRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public Stream<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDto findById(@NotNull UUID id) {
        return authorRepository.findById(id)
                .map(authorMapper::map)
                .orElseThrow(() -> EntityNotFoundException.ofAuthor(id));
    }

    @NotNull
    @Override
    @Transactional
    public AuthorDto create(@NotNull AuthorDto author) {
        return Optional.of(author)
                .map(authorMapper::map)
                .map(authorRepository::save)
                .map(authorMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    @Transactional
    public AuthorDto update(@NotNull UUID id, @NotNull AuthorDto author) {
        var existingDto = authorRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.ofAuthor(id))
                .setFirstName(author.getFirstName())
                .setLastName(author.getLastName());

        return Optional.of(existingDto)
                .map(authorRepository::save)
                .map(authorMapper::map)
                .orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(@NotNull UUID id) {
        authorRepository.deleteById(id);
    }

}
