package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;

import java.nio.file.Path;
import java.util.Map;

public interface FileService {

    boolean createFolder(String folderPath) throws ApplicationException;

    String getFileNameFromTemplate(String template, Map<String, String> params);

    Path getFilePath(String endpointFolder, String fileName);

    String getFileContentToString(Path file) throws ApplicationException;
}
