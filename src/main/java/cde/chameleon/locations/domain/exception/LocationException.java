package cde.chameleon.locations.domain.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LocationException extends RuntimeException {

    private final UUID id;

    public LocationException(UUID id, String message) {
        super(message);
        this.id = id;
    }
}
