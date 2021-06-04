package me.vcouturier.bouchon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationException extends Exception{
    private String errorCode;
    private String errorMessage;

    @Override
    public String getMessage() {
        return ("[" + errorCode + "] " + errorMessage);
    }

    public ApplicationException(Throwable exception, String errorCode, String errorMessage){
        super(exception);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
