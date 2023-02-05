package cde.chameleon.locations.api;

import cde.chameleon.api.IsUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PatchLocationDto {

    @Schema(description = "id of the location", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    @IsUUID(message = "Id of the location must be a valid UUID.")
    @NotNull(message = "Id of the location must always be provided.")
    private String id;

    @Schema(description = "name of the location", example = "ConSol Munich")
    @Size(min = 1, message = "Name of the location must not be empty.")
    private String name;

    @Schema(description = "address of the location", example = """
            ConSol Software GmbH
            St.-Cajetan-Straße 43
            81669 Munich
            Germany
            """)
    private String address;

    @Schema(description = "latitude of the location", example = "48.12062720351421")
    @DecimalMin(value = "-90", message = "Latitude must be between -90 an 90.")
    @DecimalMax(value = "90", message = "Latitude must be between -90 an 90.")
    private BigDecimal latitude;

    @Schema(description = "longitude of the location", example = "11.602475161693459")
    @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180.")
    @DecimalMax(value = "180", message = "Longitude must be between -180 and 180.")
    private BigDecimal longitude;
}
