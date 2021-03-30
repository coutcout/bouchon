package me.vcouturier.bouchon.services.endpoint.impl;

import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.model.Parameter;
import me.vcouturier.bouchon.properties.BouchonProperties;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import me.vcouturier.bouchon.services.endpoint.EndPointService;
import me.vcouturier.bouchon.services.endpoint.FileService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class EndPointServiceImpl implements EndPointService {

    @Autowired
    private BouchonProperties properties;

    @Autowired
    private FileService fileService;

    @Autowired
    private TypeRegexService typeRegexService;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    private final Map<String, EndPoint> mapEndpoint = new HashMap<>();

    @PostConstruct
    private void init() {
        for(EndPoint e: properties.getEndpoints()){
            log.debug(MessageFormat.format(MessageEnum.DEBUG_ENDPOINT_CREATION.getCode(), e.getName()));
            try {
                // Folder initialization
                if(!fileService.createFolder(e.getFolderName())){
                    log.debug(MessageFormat.format(MessageEnum.DEBUG_FOLDER_NOT_CREATED.getCode(), e.getFolderName()));
                }

                // parameters map initialization
                for(Parameter param : CollectionUtils.emptyIfNull(e.getParameters())){
                    e.getMapParameters().put(
                            param.getTag(),
                            typeRegexService.getTypeRegex(param.getType())
                                    .orElseThrow(() -> applicationExceptionFactory.createApplicationException(MessageEnum.ERR_UKNOWN_REGEX_TYPE, param.getType())));
                }

                // Caching endpoints
                mapEndpoint.put(e.getName(), e);
            } catch (ApplicationException ex) {
                log.error(MessageFormat.format(MessageEnum.ERR_ENDPOINT_INIT.getCode(), e.getName()), ex);
            }

        }
    }

    @Override
    public Optional<EndPoint> getEndPointCalled(String endpoint) {
        return Optional.ofNullable(mapEndpoint.get(endpoint));
    }
}
