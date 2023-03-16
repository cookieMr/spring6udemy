package mr.cookie.spring6udemy.model.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity(name = "authors")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AuthorEntity {

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
    @Column(columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String firstName;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "authors")
    @Builder.Default
    private Set<BookEntity> books = new HashSet<>();

    @Version
    private Integer version;

}
