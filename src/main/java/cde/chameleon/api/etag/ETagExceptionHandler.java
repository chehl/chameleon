package cde.chameleon.api.etag;

import cde.chameleon.api.ApiErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE) // This exception handler must be used before the global exception handler!
@RestControllerAdvice
@Slf4j
public class ETagExceptionHandler {

    @ExceptionHandler(NotModifiedETagException.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    public void handleNotModifiedETagException(NotModifiedETagException notModifiedETagException) {
        log.debug(notModifiedETagException.getMessage(), notModifiedETagException);
    }

    @ExceptionHandler(OutdatedETagException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ApiErrorDto handleOutdatedETagException(OutdatedETagException outdatedETagException) {
        log.debug(outdatedETagException.getMessage(), outdatedETagException);
        return new ApiErrorDto("Outdated data");
    }
}
