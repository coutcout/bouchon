package me.vcouturier.bouchon.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.vcouturier.bouchon.enums.RequestParameterPlace;
import me.vcouturier.bouchon.regex.model.CustomTypeRegex;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(of = {
        "name",
        "folderName"
})
public class EndPoint {
    // From properties
    private String name;
    private String folderName;
    private String urlTemplate;
    private String fileTemplate;
    private List<Parameter> parameters;
    private RequestParameterPlace requestParameterPlace;

    // From initialization
    private Map<String, ITypeRegex> mapParameters = new HashMap<>();
    private String urlRegex;
    private List<String> paramsAvailable = new ArrayList<>();
}
