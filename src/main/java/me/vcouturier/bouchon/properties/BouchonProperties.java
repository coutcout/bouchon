package me.vcouturier.bouchon.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.services.endpoint.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
@Log4j2
@ConfigurationProperties(prefix = "bouchon")
public class BouchonProperties {

    @Autowired
    private FileService fileService;

    @Setter
    private List<EndPoint> endpoints;

    @Setter
    private String test;

    private final Map<String, EndPoint> mapEndpoint = new HashMap<>();

    @PostConstruct
    private void init() throws ApplicationException {
        for(EndPoint e: endpoints){
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

}
