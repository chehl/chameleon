package cde.chameleon.locations.api;

import cde.chameleon.api.etag.ETaggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class LocationDto implements ETaggable {

    @Schema(description = "id of the location", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    private String id;

    @Schema(description = "name of the location", example = "ConSol Munich")
    private String name;

    @Schema(description = "address of the location", example = """
            ConSol Software GmbH
            St.-Cajetan-Straße 43
            81669 Munich
            Germany
            """)
    private String address;

    @Schema(description = "latitude of the location", example = "48.12062720351421")
    private BigDecimal latitude;

    @Schema(description = "longitude of the location", example = "11.602475161693459")
    private BigDecimal longitude;

    @Schema(description = "ETag of this location", example = "2e8eac90e2492a00fcf763cf8a8466db")
    private String eTag;
}