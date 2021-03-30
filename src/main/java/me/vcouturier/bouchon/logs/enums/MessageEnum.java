package me.vcouturier.bouchon.logs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageEnum {

    // Errors
    ERR_INVALID_ENDPOINT("ERR-INIT-001"),
    ERR_DATAFOLDER_MISSING("ERR-INIT-002"),
    ERR_UKNOWN_REGEX_TYPE("ERR-INIT-003"),
    ERR_ENDPOINT_INIT("ERR_INIT-004"),

    // Debugs
    DEBUG_ENDPOINT_CREATION("DEBUG-INIT-001"),
    DEBUG_FOLDER_NOT_CREATED("DEBUG-INIT-002"),
    ;

    private String code;
}
