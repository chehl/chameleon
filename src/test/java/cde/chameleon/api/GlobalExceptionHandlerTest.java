package cde.chameleon.api;

import cde.chameleon.ChameleonDisplayNameGenerator;
import com.google.common.truth.Truth;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
class GlobalExceptionHandlerTest {

    private void assertResponseStatus(String method, Class<? extends Exception> exception, HttpStatus expectedHttpStatus) {
        try {
            ResponseStatus responseStatus = GlobalExceptionHandler.class.getDeclaredMethod(method, exception).getAnnotation(ResponseStatus.class);
            Truth.assertThat(responseStatus).isNotNull();
            Truth.assertThat(responseStatus.value()).isEqualTo(expectedHttpStatus);
        } catch (NoSuchMethodException e) {
            Assertions.fail();
        }
    }

    @Test
    void givenRuntimeException_whenHandlingException_thenReturnsHttpStatusInternalServerError() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        RuntimeException e = new RuntimeException();

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleRuntimeException(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Internal server error");
        assertResponseStatus("handleRuntimeException", RuntimeException.class, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void givenAccessDeniedException_whenHandlingException_thenReturnsHttpStatusForbidden() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        AccessDeniedException e = new AccessDeniedException("msg");

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleAccessDeniedException(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Access denied");
        assertResponseStatus("handleAccessDeniedException", AccessDeniedException.class, HttpStatus.FORBIDDEN);
    }

    @Test
    void givenConstraintViolationException_whenHandlingException_thenReturnsHttpStatusUnprocessableEntity() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        ConstraintViolationException e = new ConstraintViolationException(Collections.emptySet());

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleConstraintViolation(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Invalid data");
        assertResponseStatus("handleConstraintViolation", ConstraintViolationException.class, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void givenMethodArgumentNotValidException_whenHandlingException_thenReturnsHttpStatusUnprocessableEntity() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        MethodArgumentNotValidException e = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(e.getFieldErrors()).thenReturn(Collections.emptyList());

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleMethodArgumentNotValidException(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Invalid data");
        assertResponseStatus("handleMethodArgumentNotValidException", MethodArgumentNotValidException.class, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void givenHttpMessageNotReadableException_whenHandlingException_thenReturnsHttpStatusBadRequest() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpMessageNotReadableException e = new HttpMessageNotReadableException("msg", Mockito.mock(HttpInputMessage.class));

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleHttpMessageNotReadableException(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Bad request");
        assertResponseStatus("handleHttpMessageNotReadableException", HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenConcurrencyFailureException_whenHandlingException_thenReturnsHttpStatusConflict() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        ConcurrencyFailureException e = new ConcurrencyFailureException("msg");

        // when
        ApiErrorDto apiErrorDto = globalExceptionHandler.handleConcurrencyFailureException(e);

        // then
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).isEqualTo("Resource conflict. Please try again.");
        assertResponseStatus("handleConcurrencyFailureException", ConcurrencyFailureException.class, HttpStatus.CONFLICT);
    }
}
