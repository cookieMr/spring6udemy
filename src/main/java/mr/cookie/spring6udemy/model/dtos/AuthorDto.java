package mr.cookie.spring6udemy.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(
            example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d"
    )
    private UUID id;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 64,
            example = "Brandon"
    )
    private String firstName;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 64,
            example = "Sanderson"
    )
    private String lastName;

}
