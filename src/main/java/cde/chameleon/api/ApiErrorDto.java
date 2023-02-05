package cde.chameleon.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ApiErrorDto {

    @Schema(description = "error message", example = "An error occurred!")
    private String message;

    @Schema(description = "errors", example = "[\"error1\", \"error2\"]")
    private List<String> errors;

    public ApiErrorDto() {
        this("");
    }

    public ApiErrorDto(String message) {
        this(message, Collections.emptyList());
    }

    public ApiErrorDto(String message, List<String> errors) {
        this.message = message;
        this.errors = new ArrayList<>(errors);
    }
}
