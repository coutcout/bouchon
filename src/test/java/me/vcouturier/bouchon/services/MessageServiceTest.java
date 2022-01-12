package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationRuntimeException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.properties.MessagesProperties;
import me.vcouturier.bouchon.services.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Mock
    private MessagesProperties messagesProperties;

    private MessageServiceImpl messageService;

    @BeforeEach
    public void init(){
        messageService = new MessageServiceImpl();
        ReflectionTestUtils.setField(messageService, "messagesProperties", messagesProperties);
        ReflectionTestUtils.setField(messageService, "applicationExceptionFactory", applicationExceptionFactory);
    }

    @Test
    public void formatMessage_messageExists(){
        // Arrange
        MessageEnum messageEnum = MessageEnum.CONFIG_ENDPOINT_DELETE_FILE_NOT_FOUND;
        String message = "file {0} deleted";
        String filename = "test";

        // Mock
        doReturn(Map.of(messageEnum.getCode(), message)).when(messagesProperties).getMessages();

        // Act
        String res = messageService.formatMessage(messageEnum, filename);

        // Assert
        assertThat(res).isNotEmpty();
        assertThat(res).isEqualTo("file test deleted");
    }

    @Test
    public void formatMessage_messageNotExists(){
        // Arrange
        MessageEnum messageEnum = MessageEnum.CONFIG_ENDPOINT_DELETE_FILE_NOT_FOUND;
        String filename = "test";

        // Mock
        doReturn(Map.of()).when(messagesProperties).getMessages();
        doReturn(new ApplicationRuntimeException("code", "message")).when(applicationExceptionFactory).createApplicationRuntimeException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        ApplicationRuntimeException applicationRuntimeException= assertThrows(ApplicationRuntimeException.class, () -> messageService.formatMessage(messageEnum, filename));

        // Assert
        assertThat(applicationRuntimeException).isNotNull();
    }

    @Test
    public void formatMessage_invalidMessage(){
        // Arrange
        MessageEnum messageEnum = null;
        String filename = "test";

        // Mock
        doReturn(new ApplicationRuntimeException("code", "message")).when(applicationExceptionFactory).createApplicationRuntimeException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationRuntimeException applicationRuntimeException= assertThrows(ApplicationRuntimeException.class, () -> messageService.formatMessage(messageEnum, filename));

        // Assert
        assertThat(applicationRuntimeException).isNotNull();
    }
}
