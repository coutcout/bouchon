package me.vcouturier.bouchon.model;

import lombok.Getter;
import lombok.Setter;
import me.vcouturier.bouchon.regex.model.CustomTypeRegex;
import me.vcouturier.bouchon.regex.model.ITypeRegex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EndPoint {
    // From properties
    private String name;
    private String folderName;
    private String urlTemplate;
    private String fileTemplate;
    private List<Parameter> parameters;

    // From initialization
    private Map<String, ITypeRegex> mapParameters = new HashMap<>();
}
