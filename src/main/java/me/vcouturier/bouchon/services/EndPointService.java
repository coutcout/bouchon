package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EndPointService {

    Optional<EndPoint> getEndPointCalled(String endpoint) throws ApplicationException;

    String runEndpoint(EndPoint e, String request) throws ApplicationException;

    List<EndPoint> loadEndpointsFromFile(MultipartFile endpointFile) throws IOException;
}
