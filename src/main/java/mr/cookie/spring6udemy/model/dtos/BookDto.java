package mr.cookie.spring6udemy.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private static final String ISBN_REGEXP = "^\\d{3}-?\\d{10}$";

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 128,
            example = "Warbreaker")
    @NotNull
    @Size(min = 1, max = 128)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 13,
            maxLength = 14,
            example = "978-0765360038",
            pattern = ISBN_REGEXP)
    @NotNull
    @Size(min = 13, max = 14)
    @Pattern(regexp = ISBN_REGEXP)
    private String isbn;

}
