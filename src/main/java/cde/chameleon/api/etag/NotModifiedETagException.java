package cde.chameleon.api.etag;

import lombok.Getter;

@Getter
public class NotModifiedETagException extends ETagException {

    private final String eTag;

    public NotModifiedETagException(String eTag) {
        super("Not Modified: The provided ETag [%s] matches the current ETag.".formatted(eTag));
        this.eTag = eTag;
    }
}
