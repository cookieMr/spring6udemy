package mr.cookie.spring6udemy.bootstrap;

import java.util.Arrays;

import mr.cookie.spring6udemy.domain.Publisher;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.apache.commons.collections4.IterableUtils;
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
    private final PublisherRepository publisherRepository;

    @PostConstruct
    private void printRepositoriesCounts() {
        log.info("Author table size: {}", this.authorRepository.count());
        log.info("Book table size: {}", this.bookRepository.count());
        log.info("Publisher table size: {}", this.publisherRepository.count());
    }

    @Override
    public void run(String... args) {
        var dragonsteelPublisher = Publisher.builder()
                .name("DragonSteel Books")
                .address("PO Box 698")
                .state("UT")
                .city("American Fork")
                .zipCode("84003")
                .build();
        var savedPublisher = this.publisherRepository.save(dragonsteelPublisher);

        var saBook1 = Book.builder()
                .title("Way of Kings")
                .isbn("978-0765365279")
                .publisher(savedPublisher)
                .build();
        var saBook2 = Book.builder()
                .title("Words of Radiance")
                .isbn("978-0765326362")
                .publisher(savedPublisher)
                .build();
        var saBook3 = Book.builder()
                .title("Oathbringer")
                .isbn("978-0765326379")
                .publisher(savedPublisher)
                .build();
        var saBook4 = Book.builder()
                .title("Rhythm of War")
                .isbn("978-0765326386")
                .publisher(savedPublisher)
                .build();
        var savedBooks = IterableUtils.toList(this.bookRepository.saveAll(
                Arrays.asList(saBook1, saBook2, saBook3, saBook4)
        ));

        var bSandersonAuthor = Author.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();
        bSandersonAuthor.getBooks().addAll(savedBooks);
        var savedSandersonAuthor = this.authorRepository.save(bSandersonAuthor);

        savedBooks.forEach(book -> book.getAuthors().add(savedSandersonAuthor));
        this.bookRepository.saveAll(savedBooks);

        this.printRepositoriesCounts();
    }

}
