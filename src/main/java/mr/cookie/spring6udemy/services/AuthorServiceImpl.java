package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.services.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    @NotNull
    private final AuthorMapper authorMapper;

    @NotNull
    private final AuthorRepository authorRepository;

    @NotNull
    @Override
    public List<Author> findAll() {
        return Optional.of(this.authorRepository)
                .map(CrudRepository::findAll)
                .map(this.authorMapper::mapToModel)
                .orElse(Collections.emptyList());
    }

    @NotNull
    @Override
    public Author findById(long id) {
        return this.authorRepository.findById(id)
                .map(this.authorMapper::map)
                .orElseThrow(() -> new NotFoundEntityException(id, Author.class));
    }

    @NotNull
    @Override
    public Author create(@NotNull Author author) {
        return Optional.of(author)
                .map(this.authorMapper::map)
                .map(this.authorRepository::save)
                .map(this.authorMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    public Author update(long id, @NotNull Author author) {
        var existingDto = this.authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id, Author.class));

        existingDto.setFirstName(author.getFirstName());
        existingDto.setLastName(author.getLastName());

        return Optional.of(existingDto)
                .map(this.authorRepository::save)
                .map(this.authorMapper::map)
                .orElseThrow();
    }

    @Override
    public void deleteById(long id) {
        var doesAuthorExist = this.authorRepository.findById(id)
                .isPresent();

        if (doesAuthorExist) {
            this.authorRepository.deleteById(id);
        } else {
            throw new NotFoundEntityException(id, Author.class);
        }
    }

}
