package me.vcouturier.bouchon.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.model.EndPoint;
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

    @Setter
    private List<EndPoint> endpoints;

    @Setter
    private String test;

    private Map<String, EndPoint> mapEndpoint = new HashMap<>();

    @PostConstruct
    private void init(){
        for(EndPoint e: endpoints){
            log.debug("Enregistrement du endpoint {}", e.getName());
            mapEndpoint.put(e.getName(), e);
        }
    }

}
