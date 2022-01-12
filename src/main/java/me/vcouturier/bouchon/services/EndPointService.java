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

    String runEndpointGet(EndPoint e, String request) throws ApplicationException;

    String runEndpointPost(EndPoint endPoint, String request, Map<String, String> params) throws ApplicationException;

    Map<EndPoint, Optional<String>> loadEndpointsFromFile(File endpointFile) throws IOException;

    void reinitializeEndpoints();
}
