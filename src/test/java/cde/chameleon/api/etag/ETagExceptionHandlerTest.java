package cde.chameleon.api.etag;

import cde.chameleon.ChameleonDisplayNameGenerator;
import cde.chameleon.api.ApiErrorDto;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
class ETagExceptionHandlerTest {

    private void assertResponseStatus(String method, Class<? extends Exception> exception, HttpStatus expectedHttpStatus) {
        try {
            ResponseStatus responseStatus = ETagExceptionHandler.class.getDeclaredMethod(method, exception).getAnnotation(ResponseStatus.class);
            Truth.assertThat(responseStatus).isNotNull();
            Truth.assertThat(responseStatus.value()).isEqualTo(expectedHttpStatus);
        } catch (NoSuchMethodException _) {
            Assertions.fail();
        }
    }

    @Test
    void givenNotModifiedEtagException_whenHandlingException_thenReturnsHttpStatusNotModified() {
        // given
        ETagExceptionHandler eTagExceptionHandler = new ETagExceptionHandler();
        NotModifiedETagException e = new NotModifiedETagException("eTag");

        // when
        eTagExceptionHandler.handleNotModifiedETagException(e);

        // then
        assertResponseStatus("handleNotModifiedETagException", NotModifiedETagException.class, HttpStatus.NOT_MODIFIED);
    }

    @Test
    void givenOutdatedEtagException_whenHandlingException_thenReturnsHttpStatusPreconditionFailed() {
        // given
        ETagExceptionHandler eTagExceptionHandler = new ETagExceptionHandler();
        OutdatedETagException e = new OutdatedETagException("ifMatchETag", "actualETag");

        // when
        ApiErrorDto apiErrorDto = eTagExceptionHandler.handleOutdatedETagException(e);

        // then
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Outdated data");
        assertResponseStatus("handleOutdatedETagException", OutdatedETagException.class, HttpStatus.PRECONDITION_FAILED);
    }
}
