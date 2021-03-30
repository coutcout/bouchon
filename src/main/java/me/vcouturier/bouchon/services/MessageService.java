package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.logs.enums.MessageEnum;

public interface MessageService {
    String formatMessage(MessageEnum message, String... args);
}
