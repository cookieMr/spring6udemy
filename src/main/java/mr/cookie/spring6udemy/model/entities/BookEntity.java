package mr.cookie.spring6udemy.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity(name = "books")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class BookEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "varchar", length = 36,
            unique = true, nullable = false, insertable = false, updatable = false)
    private UUID id;

    @Column(columnDefinition = "varchar", length = 128, nullable = false)
    private String title;

    @Column(columnDefinition = "varchar", length = 14, unique = true, nullable = false)
    private String isbn;

    @ManyToMany
    @JoinTable(name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Builder.Default
    private Set<AuthorEntity> authors = new HashSet<>();

    @ManyToOne
    private PublisherEntity publisher;

    @Version
    @Column(nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

}
