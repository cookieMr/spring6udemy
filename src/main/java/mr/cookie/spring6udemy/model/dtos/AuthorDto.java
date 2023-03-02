package mr.cookie.spring6udemy.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(
            example = "123",
            pattern = "^\\d+$",
            minimum = "0"
    )
    private Long id;

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
