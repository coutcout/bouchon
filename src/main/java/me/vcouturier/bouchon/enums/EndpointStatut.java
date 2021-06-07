package me.vcouturier.bouchon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.vcouturier.bouchon.logs.enums.MessageEnum;

@Getter
@AllArgsConstructor
public enum EndpointStatut {
    OK(MessageEnum.ENDPOINT_STATUT_OK),
    KO(MessageEnum.ENDPOINT_STATUT_KO);

    private MessageEnum messageCode;

}
