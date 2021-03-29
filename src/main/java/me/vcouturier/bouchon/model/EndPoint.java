package me.vcouturier.bouchon.model;

import lombok.Getter;
import lombok.Setter;
import me.vcouturier.bouchon.regex.model.CustomTypeRegex;

import java.util.Map;

@Getter
@Setter
public class EndPoint {
    private String name;
    private String folderName;
    private String urlTemplate;
    private String fileTemplate;
    private Map<String, String> parameters;
}
