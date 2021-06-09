package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface EndPointService {

    Optional<EndPoint> getEndPointCalled(String endpoint) throws ApplicationException;

    String runEndpointGet(EndPoint e, String request) throws ApplicationException;

    String runEndpointPost(EndPoint endPoint, String request, Map<String, String> params) throws ApplicationException;

    Map<EndPoint, Optional<String>> loadEndpointsFromFile(File endpointFile) throws IOException;

    void reinitializeEndpoints();
}
