package com.mikkkkkkka.gateway.exception;

import lombok.Getter;

@Getter
public class TransferredHttpException extends RuntimeException {
    private Integer status;
    private RuntimeException error;

    public TransferredHttpException(String message) {
        super(message);
    }

    public TransferredHttpException(int status, RuntimeException error) {
        this(error.getMessage());
        this.status = status;
        this.error = error;
    }
}
