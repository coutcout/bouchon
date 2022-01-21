package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;

import java.nio.file.Path;
import java.util.Map;

public interface FileService {

    /**
     * Method to create a folder in the data folder
     * @param folderName {@link String} Name of folder to create
     * @return
     * <ul>
     *     <li>true - the file was created successfully</li>
     *     <li>false - Folder could not be created</li>
     * </ul>
     * @throws ApplicationException
     * <ul>
     *     <li>{@link MessageEnum#CONFIG_UPLOAD_INVALID_FOLDERNAME} - If the name of the folder to create is null or empty</li>
     *     <li>{@link MessageEnum#ERR_DATAFOLDER_MISSING} - If the data folder does not exist or is not a folder</li>
     * </ul>
     */
    boolean createFolder(String folderName) throws ApplicationException;

    /**
     * Method to generate the name of a file from a template and parameters
     * @param template {@link String} File name template. If a variable is not contained in the map, it will not be replaced in the result.
     * @param params {@link Map} Parameter map to override template variables. The key must match the parameter name.
     * @return The template with the replaced variables
     */
    String getFileNameFromTemplate(String template, Map<String, String> params) throws ApplicationException;

    Path getFilePath(String endpointFolder, String fileName);

    String getFileContentToString(Path file) throws ApplicationException;
}
