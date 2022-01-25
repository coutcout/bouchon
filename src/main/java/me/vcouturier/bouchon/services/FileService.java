package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;

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

    /**
     * Method to retrieve the absolute path of a file for an endpoint
     * @param endpointFolder {@link EndPoint} for which we are looking for a file
     * @param fileName {@link String} name of file sought
     * @return {@link Path} representing the absolute path of the sought file
     */
    Path getFilePath(EndPoint endpointFolder, String fileName);

    /**
     * Method to retrieve the contents of a file
     * @param file {@link Path} of the file whose content we want to retrieve
     * @return {@link String} File content
     * @throws ApplicationException - If an error occurred while reading the file
     */
    String getFileContentToString(Path file) throws ApplicationException;
}
