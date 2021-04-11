package me.vcouturier.bouchon.exceptions;

public class EndPointNotFoundException extends ClientException{
    public EndPointNotFoundException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
