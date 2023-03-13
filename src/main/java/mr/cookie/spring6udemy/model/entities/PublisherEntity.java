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
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "publishers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class PublisherEntity {

    @Id
    @GeneratedValue(generator = Constant.UUID_NAME)
    @GenericGenerator(name = Constant.UUID_NAME, strategy = Constant.UUID_GENERATOR_STRATEGY)
    @Column(
            columnDefinition = "varchar", length = 36,
            unique = true, nullable = false, insertable = false, updatable = false
    )
    private UUID id;

    @Column(columnDefinition = "varchar", length = 128, nullable = false)
    private String name;

    @Column(columnDefinition = "varchar", length = 128, nullable = false)
    private String address;

    @Column(columnDefinition = "varchar", length = 64, nullable = false)
    private String city;

    @Column(columnDefinition = "varchar", length = 64, nullable = false)
    private String state;

    @Column(columnDefinition = "varchar", length = 64, nullable = false)
    private String zipCode;

    @Builder.Default
    @OneToMany(mappedBy = "publisher")
    private Set<BookEntity> books = new HashSet<>();

    @Version
    private Integer version;

}
