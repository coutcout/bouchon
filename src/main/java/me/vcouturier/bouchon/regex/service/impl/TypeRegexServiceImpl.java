package me.vcouturier.bouchon.regex.service.impl;

import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.regex.enums.TypeRegex;
import me.vcouturier.bouchon.regex.model.CustomTypeRegex;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import me.vcouturier.bouchon.regex.properties.CustomTypeRegexProperties;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class TypeRegexServiceImpl implements TypeRegexService {

    final private Map<String, ITypeRegex> mapTypeRegex = new HashMap<>();

    @Autowired
    private CustomTypeRegexProperties customTypeRegexProperties;

    @PostConstruct
    private void initMapTypeRegex(){
        // Adding default regex
        for(ITypeRegex typeRegex: TypeRegex.values()){
            mapTypeRegex.put(typeRegex.getType(), typeRegex);
            log.debug("Ajout de la regex de type {} et de format {}", typeRegex.getType(), typeRegex.getRegex());
        }

        // Adding custom regex from properties file
        for(Map.Entry<String, String> typeRegex: customTypeRegexProperties.getRegex().entrySet()){
            mapTypeRegex.put(typeRegex.getKey(), new CustomTypeRegex(typeRegex.getKey(), typeRegex.getValue()));
            log.debug("Ajout de la regex custom de type {} et de format {}", typeRegex.getKey(), typeRegex.getValue());
        }
    }

    @Override
    public Optional<ITypeRegex> getTypeRegex(String type) {
        return Optional.ofNullable(mapTypeRegex.get(type));
    }
}
