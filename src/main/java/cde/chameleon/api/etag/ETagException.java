package cde.chameleon.api.etag;

public abstract class ETagException extends RuntimeException {

    protected ETagException(String message) {
        super(message);
    }
}
