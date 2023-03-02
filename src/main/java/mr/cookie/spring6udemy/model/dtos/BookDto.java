package mr.cookie.spring6udemy.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    @Schema(
            pattern = "^\\d+$",
            minimum = "0",
            example = "123"
    )
    private Long id;

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
