package mr.cookie.spring6udemy.bootstrap;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mr.cookie.spring6udemy.domain.Author;
import mr.cookie.spring6udemy.domain.Book;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.repositories.BookRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @PostConstruct
    private void printRepositoriesCounts() {
        log.info("Author table size: {}", this.authorRepository.count());
        log.info("Book table size: {}", this.bookRepository.count());
    }

    @Override
    public void run(String... args) {
        var bSandersonAuthor = Author.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();

        var saBook1 = Book.builder()
                .title("Way of Kings")
                .isbn("978-0765365279")
                .build();
        var saBook2 = Book.builder()
                .title("Words of Radiance")
                .isbn("978-0765326362")
                .build();
        var saBook3 = Book.builder()
                .title("Oathbringer")
                .isbn("978-0765326379")
                .build();
        var saBook4 = Book.builder()
                .title("Rhythm of War")
                .isbn("978-0765326386")
                .build();

        var savedSandersonAuthor = this.authorRepository.save(bSandersonAuthor);
        var savedSaBook1 = this.bookRepository.save(saBook1);
        var savedSaBook2 = this.bookRepository.save(saBook2);
        var savedSaBook3 = this.bookRepository.save(saBook3);
        var savedSaBook4 = this.bookRepository.save(saBook4);

        savedSandersonAuthor.getBooks().addAll(Arrays.asList(
            savedSaBook1, savedSaBook2, savedSaBook3, savedSaBook4
        ));
        this.authorRepository.save(savedSandersonAuthor);

        this.printRepositoriesCounts();
    }

}
