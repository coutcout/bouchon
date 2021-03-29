package me.vcouturier.bouchon.regex.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.vcouturier.bouchon.regex.model.ITypeRegex;

@Getter
@AllArgsConstructor
public enum TypeRegex implements ITypeRegex {
    STRING("string", "\\w+?"),
    NUMBER("number", "\\d+?"),
    BOOLEAN("boolean", "true|false")
    ;

    private String type;
    private String regex;
}
