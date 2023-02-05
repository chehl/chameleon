package cde.chameleon.api.etag;

import lombok.Getter;

@Getter
public class OutdatedETagException extends ETagException {

    private final String ifMatch;
    private final String actualETag;

    public OutdatedETagException(String ifMatch, String actualETag) {
        super("Outdated ETag: The provided ETag [%s] does not match the current ETag [%s].".formatted(ifMatch, actualETag));
        this.ifMatch = ifMatch;
        this.actualETag = actualETag;
    }
}
