package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface CrudService<T> {

    /**
     * A default page number pointing to the very first page. It's 0-based index.
     */
    int DEFAULT_PAGE_NUMBER = 0;

    @NotNull
    Page<T> findAll(@Nullable Integer pageNumber, @Nullable Integer pageSize);

    Optional<T> findById(@NotNull UUID id);

    @NotNull
    @Transactional
    T create(@NotNull T author);

    @NotNull
    @Transactional
    T update(@NotNull UUID id, @NotNull T author);

    /**
     * Returns {@code true} if en entity with provided ID was removed, {@code false} otherwise.
     * @param id id of an entity to remove
     * @return {@code true} if an entity was removed, {@code false} otherwise
     */
    @Transactional
    boolean deleteById(@NotNull UUID id);

    default int validateDefaultPageSize(int defaultPageSize) {
        if (defaultPageSize <= 0) {
            throw new IllegalArgumentException("The default pageSize should be greater than zero.");
        }

        return defaultPageSize;
    }

    @NotNull
    default PageRequest createPageRequest(
            @Nullable Integer pageNumber,
            @Nullable Integer pageSize,
            int defaultPageSize
    ) {
        var nonNullPageNumber = Optional.ofNullable(pageNumber)
                .orElse(DEFAULT_PAGE_NUMBER);
        var nonNullPageSize = Optional.ofNullable(pageSize)
                .orElse(defaultPageSize);

        return PageRequest.of(
                nonNullPageNumber,
                nonNullPageSize,
                Sort.by("createdDate")
        );
    }

}
