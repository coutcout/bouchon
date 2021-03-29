package me.vcouturier.bouchon.services.endpoint.impl;

import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.properties.BouchonProperties;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import me.vcouturier.bouchon.services.endpoint.EndPointService;
import me.vcouturier.bouchon.services.endpoint.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class EndPointServiceImpl implements EndPointService {

    @Autowired
    private BouchonProperties properties;

    @Autowired
    private FileService fileService;

    @Autowired
    private TypeRegexService typeRegexService;

    private final Map<String, EndPoint> mapEndpoint = new HashMap<>();

    @PostConstruct
    private void init() throws ApplicationException {
        for(EndPoint e: properties.getEndpoints()){
            log.debug("Enregistrement du endpoint {}", e.getName());
            try {
                // Folder initialization
                if(!fileService.createFolder(e.getFolderName())){
                    log.debug("Le dossier {} n'a pas été initialisé", e.getFolderName());
                }

                // Caching endpoints
                mapEndpoint.put(e.getName(), e);
            } catch (ApplicationException ex) {
                log.error("Une erreur est survenue lors de l'initialisation du endpoint {}", e.getName(), ex);
                throw ex;
            }

        }
    }

    @Override
    public Optional<EndPoint> getEndPointCalled(String endpoint) {
        return Optional.ofNullable(mapEndpoint.get(endpoint));
    }
}
