package me.vcouturier.bouchon.exceptions.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorsEnum {

    INVALID_ENDPOINT ("ERR-INIT-001"),
    DATAFOLDER_MISSING ("ERR-INIT-002");

    private String code;
}
