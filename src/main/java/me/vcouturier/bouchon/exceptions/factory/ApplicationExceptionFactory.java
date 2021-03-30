package me.vcouturier.bouchon.exceptions.factory;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.ApplicationRuntimeException;
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
        return new ApplicationException(error.getCode(), this.getMessage(error));
    }

    public ApplicationException createApplicationException(MessageEnum error, String... variables){
        return new ApplicationException(error.getCode(), MessageFormat.format(this.getMessage(error), (Object[]) variables));
    }

    public ApplicationRuntimeException createApplicationRuntimeException(MessageEnum error){
        return new ApplicationRuntimeException(error.getCode(), this.getMessage(error));
    }

    public ApplicationRuntimeException createApplicationRuntimeException(MessageEnum error, String... variables){
        return new ApplicationRuntimeException(error.getCode(), MessageFormat.format(this.getMessage(error), (Object[]) variables));
    }

    private String getMessage(MessageEnum message){
        String messageString = messagesProperties.getMessages().get(message.getCode());
        if(messageString == null){
            throw new IllegalArgumentException("Invalid message enum");
        }

        return messageString;
    }
}
