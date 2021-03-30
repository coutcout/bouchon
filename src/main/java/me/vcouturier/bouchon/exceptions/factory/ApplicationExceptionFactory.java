package me.vcouturier.bouchon.exceptions.factory;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.properties.MessagesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class ApplicationExceptionFactory {

    @Autowired
    private MessagesProperties messagesProperties;

    public ApplicationException createApplicationException(MessageEnum error){
        return new ApplicationException(error.getCode(), messagesProperties.getMessages().get(error.getCode()));
    }

    public ApplicationException createApplicationException(MessageEnum error, String... variables){
        return new ApplicationException(error.getCode(), MessageFormat.format(messagesProperties.getMessages().get(error.getCode()), (Object[]) variables));
    }
}
