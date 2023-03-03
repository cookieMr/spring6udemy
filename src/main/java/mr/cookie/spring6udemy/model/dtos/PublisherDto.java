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
public class PublisherDto {

    @Schema(
            example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d"
    )
    private UUID id;

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
