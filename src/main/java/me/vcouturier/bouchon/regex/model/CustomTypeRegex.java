package me.vcouturier.bouchon.regex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomTypeRegex implements ITypeRegex{
    private String type;
    private String regex;
}
