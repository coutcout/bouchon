package me.vcouturier.bouchon.exceptions.factory;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.enums.ErrorsEnum;
import me.vcouturier.bouchon.properties.ErrorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationExceptionFactory {

    @Autowired
    private ErrorProperties errorProperties;

    public ApplicationException createApplicationException(ErrorsEnum error){
        return new ApplicationException(error.getCode(), errorProperties.getErrors().get(error.getCode()));
    }
}
