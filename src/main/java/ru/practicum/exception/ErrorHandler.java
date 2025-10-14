package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private ErrorDto returnErrorData(String error, Throwable exception) {
        return new ErrorDto(error, exception.getMessage());
    }

    @ExceptionHandler({InvalidDataException.class})
    protected ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex, WebRequest request) {
        ErrorDto errorDto = returnErrorData(ex.getMessage(), ex);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidEmailException.class})
    protected ResponseEntity<Object> handleInvalidEmailException(InvalidEmailException ex, WebRequest request) {
        ErrorDto errorDto = returnErrorData(ex.getMessage(), ex);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
        ErrorDto errorDto = returnErrorData(ex.getMessage(), ex);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}