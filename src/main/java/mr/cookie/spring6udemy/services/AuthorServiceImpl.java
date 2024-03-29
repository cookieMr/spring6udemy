package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::map)
                .toList();
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
        var doesAuthorExist = authorRepository.findByFirstNameAndLastName(
                author.getFirstName(),
                author.getLastName())
                .isPresent();
        if (doesAuthorExist) {
            throw EntityExistsException.ofAuthor();
        }

        return Optional.of(author)
                .map(authorMapper::map)
                .map(authorRepository::save)
                .map(authorMapper::map)
                .orElseThrow();
    }

    @NotNull
    @Override
    @Transactional
    public AuthorDto update(@NotNull UUID id, @NotNull AuthorDto author) {
        var existingDto = authorRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.ofAuthor(id));

        var authorOptional = authorRepository.findByFirstNameAndLastName(
                author.getFirstName(),
                author.getLastName());
        if (authorOptional.isPresent()) {
            throw EntityExistsException.ofAuthor();
        }

        existingDto.setFirstName(author.getFirstName())
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
