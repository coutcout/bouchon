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
    ERR_ENDPOINT_INIT("ERR-INIT-004"),
    ERR_URL_UNKOWN_REGEX("ERR-INIT-005"),
    ERR_URL_DUPLICATE_REGEX("ERR-INIT-006"),
    ERR_MESSAGE_NOT_FOUND("ERR-MSG-001"),

    // Debugs
    DEBUG_ENDPOINT_CREATION("DEBUG-INIT-001"),
    DEBUG_FOLDER_NOT_CREATED("DEBUG-INIT-002"),
    DEBUG_REGEX_EXTRACTION_FOUND("DEBUG-REGEX-001")
    ;

    private String code;
}
