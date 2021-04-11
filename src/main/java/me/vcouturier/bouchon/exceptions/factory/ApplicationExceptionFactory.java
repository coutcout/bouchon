package me.vcouturier.bouchon.exceptions.factory;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.ApplicationRuntimeException;
import me.vcouturier.bouchon.exceptions.EndPointNotFoundException;
import me.vcouturier.bouchon.filters.RequestIdFilter;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.properties.MessagesProperties;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.UUID;

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

    public ApplicationException createEndPointNotFoundException(MessageEnum error){
        return new EndPointNotFoundException(error.getCode(), this.getMessage(error));
    }

    public ApplicationException createEndPointNotFoundException(MessageEnum error, String... variables){
        return new EndPointNotFoundException(error.getCode(), MessageFormat.format(this.getMessage(error), (Object[]) variables));
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
