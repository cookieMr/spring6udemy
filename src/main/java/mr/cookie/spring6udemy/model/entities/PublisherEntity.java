package mr.cookie.spring6udemy.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "publishers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class PublisherEntity {

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
    private String name;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(128)", length = 128, nullable = false)
    private String address;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String city;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String state;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String zipCode;

    @Builder.Default
    @OneToMany(mappedBy = "publisher")
    private Set<BookEntity> books = new HashSet<>();

    @Version
    @Column(nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

}
