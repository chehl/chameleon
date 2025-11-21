package cde.chameleon.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDto handleRuntimeException(RuntimeException runtimeException) {
        log.error("Unexpected runtime exception", runtimeException);
        return new ApiErrorDto("Internal server error");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorDto handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        log.debug("Access denied", accessDeniedException);
        return new ApiErrorDto("Access denied");
    }

    @ApiResponse(responseCode = "422", description = "Invalid data")
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ApiErrorDto handleConstraintViolation(ConstraintViolationException constraintViolationException) {
        log.info("Constraint violation", constraintViolationException);
        return new ApiErrorDto(
                "Invalid data",
                constraintViolationException.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()
        );
    }

    @ApiResponse(responseCode = "422", description = "Invalid data")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ApiErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.info("Method argument not valid", methodArgumentNotValidException);
        return new ApiErrorDto(
                "Invalid data",
                methodArgumentNotValidException.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList()
        );
    }

    @ApiResponse(responseCode = "400", description = "Bad request")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        log.info("Bad request", httpMessageNotReadableException);
        return new ApiErrorDto("Bad request");
    }

    @ApiResponse(responseCode = "409", description = "Resource conflict")
    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleConcurrencyFailureException(ConcurrencyFailureException concurrencyFailureException) {
        log.info("Concurrency failure", concurrencyFailureException);
        return new ApiErrorDto("Resource conflict. Please try again.");
    }
}
