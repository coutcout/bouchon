package me.vcouturier.bouchon.regex.service;

import me.vcouturier.bouchon.regex.model.ITypeRegex;

import java.util.Optional;

public interface TypeRegexService {
    Optional<ITypeRegex> getTypeRegex(String type);


}
