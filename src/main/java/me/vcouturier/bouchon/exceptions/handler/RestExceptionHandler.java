package me.vcouturier.bouchon.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.ApplicationRuntimeException;
import me.vcouturier.bouchon.exceptions.EndPointNotFoundException;
import me.vcouturier.bouchon.filters.RequestIdFilter;
import me.vcouturier.bouchon.model.ErrorResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            EndPointNotFoundException.class
    })
    @Order(1)
    protected ResponseEntity<ErrorResponse> handleEndpointNotFound(
            EndPointNotFoundException ex,
            WebRequest request
    ){
        log.error("Exception: ", ex);
        ErrorResponse response = new ErrorResponse(
                ThreadContext.get(RequestIdFilter.REQUEST_ID_HEADER),
                ex.getErrorCode(),
                ex.getErrorMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            ApplicationException.class
    })
    @Order(100)
    protected ResponseEntity<ErrorResponse> handleEndpointNotFound(
             ApplicationException ex,
            WebRequest request
    ){
        log.error("Exception: ", ex);
        ErrorResponse response = new ErrorResponse(
                ThreadContext.get(RequestIdFilter.REQUEST_ID_HEADER),
                ex.getErrorCode(),
                ex.getErrorMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {
            ApplicationRuntimeException.class
    })
    @Order(101)
    protected ResponseEntity<ErrorResponse> handleEndpointNotFound(
            ApplicationRuntimeException ex,
            WebRequest request
    ){
        log.error("Exception: ", ex);
        ErrorResponse response = new ErrorResponse(
                ThreadContext.get(RequestIdFilter.REQUEST_ID_HEADER),
                ex.getErrorCode(),
                ex.getErrorMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
