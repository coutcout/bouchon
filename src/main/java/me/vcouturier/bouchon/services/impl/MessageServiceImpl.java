package me.vcouturier.bouchon.services.impl;

import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.properties.MessagesProperties;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessagesProperties messagesProperties;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Override
    public String formatMessage(MessageEnum message, String... args){
        if(message == null){
            throw applicationExceptionFactory.createApplicationRuntimeException(MessageEnum.ERR_MESSAGE_INVALID);
        }

        String messageString = messagesProperties.getMessages().get(message.getCode());
        if(messageString == null){
            throw applicationExceptionFactory.createApplicationRuntimeException(MessageEnum.ERR_MESSAGE_NOT_FOUND, message.getCode());
        }
        return MessageFormat.format(messageString, (Object[]) args);
    }
}
