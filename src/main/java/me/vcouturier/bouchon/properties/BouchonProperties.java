package me.vcouturier.bouchon.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.model.EndPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@Slf4j
@ConfigurationProperties(prefix = "bouchon")
public class BouchonProperties {

    @Setter
    private List<EndPoint> endpoints;

    @Setter
    private String test;

}
