package me.vcouturier.bouchon.exceptions;

public abstract class ClientException extends ApplicationException{

    public ClientException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
