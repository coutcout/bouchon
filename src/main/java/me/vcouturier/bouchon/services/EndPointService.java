package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface EndPointService {

    /**
     * Method used to obtain the endpoint corresponding to the requested URL.<br/>
     * The requested URL is compared to all endpoint regexes.
     *
     * @param endpoint Requested URL
     * @return The endpoint whose regex matches the requested URL
     * @throws ApplicationException
     * <ul>
     *     <li>{@link MessageEnum#ENDPOINT_NOT_UNIQUE} - If more than one endpoint matches the requested URL</li>
     *     <li>{@link MessageEnum#CONFIG_ENDPOINT_REQUESTED_URL_INVALID} - If the requested URL is null or empty</li>
     * </ul>
     */
    Optional<EndPoint> getEndPointCalled(String endpoint) throws ApplicationException;

    /**
     * Method for processing a GET request from the Endpoint found upstream.
     * @param e {@link EndPoint} to process the request
     * @param request {@link String} GET request to process
     * @return The content of the file found thanks to the request
     * @throws ApplicationException
     * <ul>
     *     <li>{@link MessageEnum#REQUEST_MISFORMATED} - If the request does not match the Endpoint</li>
     *     <li>{@link MessageEnum#FILE_ERR_READING} - If an error occurs during file recovery</li>
     * </ul>
     */
    String runEndpointGet(EndPoint e, String request) throws ApplicationException;

    /**
     * Method for processing a POST request from the Endpoint found upstream.
     * @param endPoint {@link EndPoint} to process the request
     * @param request {@link String} POST request to process
     * @param params {@link Map} Map representing the parameters in the body of the POST request
     * @return The content of the file found thanks to the request
     * @throws ApplicationException
     * <ul>
     *     <li>{@link MessageEnum#REQUEST_MISFORMATED} - If the request does not match the Endpoint</li>
     *     <li>{@link MessageEnum#FILE_ERR_READING} - If an error occurs during file recovery</li>
     * </ul>
     */
    String runEndpointPost(EndPoint endPoint, String request, Map<String, String> params) throws ApplicationException;

    /**
     * Méthode permettant de charger et initialiser l'ensemble des endpoints d'un fichier reçu
     * @param endpointFile {@link File} File including the description of endpoints
     * @return {@link Map} Map associating an endpoint with its potential error message that occurred during its initialization
     * @throws IOException If a problem occurs while loading the file
     */
    Map<EndPoint, Optional<String>> loadEndpointsFromFile(File endpointFile) throws IOException;

    /**
     * Method to clear all Endpoints stored in the cache
     */
    void reinitializeEndpoints();
}
