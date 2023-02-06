package cde.chameleon.locations.domain.exception;

import java.util.UUID;

/**
 * The exception NotFoundLocationException shall be thrown if a location cannot be found in the repository.
 */
public class NotFoundLocationException extends LocationException {

    public NotFoundLocationException(UUID id) {
        super(id, "Location with id [%s] has not been found!".formatted(id));
    }

    public NotFoundLocationException(String name) {
        super(null, "Location with name [%s] has not been found!".formatted(name));
    }
}
