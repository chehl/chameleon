package cde.chameleon.locations.domain.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * The exception NameNotUniqueLocationException shall be thrown if someone tries to set the name of a location
 * to a name that is already in use by another location.
 */
@Getter
public class NameNotUniqueLocationException extends LocationException {

    private final String name;

    public NameNotUniqueLocationException(UUID id, String name) {
        super(id, "Location name [%s] for location with id [%s] is not unique!".formatted(name, id));
        this.name = name;
    }
}
