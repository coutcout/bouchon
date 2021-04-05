package me.vcouturier.bouchon.regex.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@Slf4j
@ConfigurationProperties(prefix = "bouchon")
public class CustomTypeRegexProperties {
    private Map<String, String> regex;
}
