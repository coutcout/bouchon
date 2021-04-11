package me.vcouturier.bouchon.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends Response{
    String code;
    String message;

    public ErrorResponse(String requestId, String errorCode, String message) {
        super(requestId);
        this.code = errorCode;
        this.message = message;
    }
}
