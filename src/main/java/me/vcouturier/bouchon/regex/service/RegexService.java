package me.vcouturier.bouchon.regex.service;

import me.vcouturier.bouchon.regex.model.ITypeRegex;

import java.util.List;
import java.util.Map;

public interface RegexService {

    /**
     * Method to retrieve all regex names from a template
     * @param string {@link String} Template
     * @return List of all regex names available in the template passed as a parameter
     */
    List<String> getRegexNameFromString(String string);

    String getRegexFormatedString(String string, Map<String, ITypeRegex> parameters);
}
