package me.vcouturier.bouchon.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.model.EndPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@Log4j2
@ConfigurationProperties(prefix = "bouchon")
public class BouchonProperties {

    @Setter
    private List<EndPoint> endpoints;

    @Setter
    private String test;

}
