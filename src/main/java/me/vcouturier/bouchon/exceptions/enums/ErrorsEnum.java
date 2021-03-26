package me.vcouturier.bouchon.exceptions.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorsEnum {

    INVALID_ENDPOINT ("ERR-001");

    private String code;
}
