package mr.cookie.spring6udemy.model.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    @Schema(
            example = "123",
            pattern = "^\\d+$",
            minimum = "0"
    )
    private Long id;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 128,
            example = "DragonSteel Books"
    )
    private String name;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 128,
            example = "PO Box 698"
    )
    private String address;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 64,
            example = "American Fork"
    )
    private String city;

    @Schema(
            minLength = 1,
            maxLength = 64,
            example = "UT"
    )
    private String state;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 64,
            example = "84003"
    )
    private String zipCode;

}
