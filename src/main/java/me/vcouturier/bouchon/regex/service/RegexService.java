package me.vcouturier.bouchon.regex.service;

import me.vcouturier.bouchon.regex.model.ITypeRegex;

import java.util.List;
import java.util.Map;

public interface RegexService {

    List<String> getRegexFromString(String string);

    String getRegexFormatedString(String string, Map<String, ITypeRegex> parameters);
}
