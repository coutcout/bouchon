package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;

import java.util.Map;

public interface FileService {

    boolean createFolder(String folderPath) throws ApplicationException;

    String getFileNameFromTemplate(String template, Map<String, String> params);
}
