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
    ERR_REGEX_NOT_EQUALS("ERR-INIT-007"),
    REQUEST_MISFORMATED("REQUEST-001"),

    // Messages lors des requêtes
    ENDPOINT_NOT_UNIQUE("ENDPOINT-001"),
    ENDPOINT_NOT_FOUND("ENDPOINT-002"),

    // Debugs,
    DEBUG_ENDPOINT_CREATION("DEBUG-INIT-001"),
    DEBUG_FOLDER_NOT_CREATED("DEBUG-INIT-002"),
    DEBUG_REGEX_EXTRACTION_FOUND("DEBUG-REGEX-001"),
    DEBUG_REGEX_REPLACING_VALUE("DEBUG-REGEX-002"),

    DEBUG_FILE_REPLACING_VALUE("DEBUG-FILE-001"),
    FILE_ERR_READING("FILE-READING-001"),

    // Config
    CONFIG_ENDPOINT_UPLOAD("CONFIG-ENDPOINT-UPLOAD");

    private String code;
}
