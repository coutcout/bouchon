package me.vcouturier.bouchon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationRuntimeException extends RuntimeException{
    private String errorCode;
    private String errorMessage;

    @Override
    public String getMessage() {
        return ("[" + errorCode + "] " + errorMessage);
    }
}
