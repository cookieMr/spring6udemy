package mr.cookie.spring6udemy.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    @Schema(
            example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d"
    )
    private UUID id;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 128,
            example = "Warbreaker"
    )
    private String title;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 13,
            maxLength = 14,
            example = "978-0765360038",
            pattern = "^\\d{3}-?\\d{10}$"
    )
    private String isbn;

}
