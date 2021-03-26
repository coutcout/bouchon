package me.vcouturier.bouchon.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@Log4j2
@ConfigurationProperties(prefix = "bouchon")
public class ErrorProperties {
    private Map<String, String> errors;
}
