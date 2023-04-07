package mr.cookie.spring6udemy.model.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity(name = "books")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class BookEntity {

    @Id
    @GeneratedValue(generator = Constant.UUID_NAME)
    @GenericGenerator(name = Constant.UUID_NAME, strategy = Constant.UUID_GENERATOR_STRATEGY)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            columnDefinition = "varchar(36)", length = 36,
            unique = true, nullable = false, insertable = false, updatable = false
    )
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(128)", length = 128, nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(14)", length = 14, unique = true, nullable = false)
    private String isbn;

    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
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
