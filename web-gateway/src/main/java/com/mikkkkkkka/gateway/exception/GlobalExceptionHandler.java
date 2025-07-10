package com.mikkkkkkka.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikkkkkkka.common.exception.ServiceUnavailableException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferredHttpException.class)
    public ResponseEntity<ErrorResponse> handleTransferredHttpException(TransferredHttpException e) {
        var error = new ErrorResponse(
                e.getStatus(),
                HttpStatus.valueOf(e.getStatus()).toString(),
                e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException() {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                "Invalid payload");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable() {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.toString(),
                "Cat or Owner Service unavailable");
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AmqpConnectException.class)
    public ResponseEntity<ErrorResponse> handleAmpqConnect() {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.toString(),
                "Message Queue Service Unavailable");
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
