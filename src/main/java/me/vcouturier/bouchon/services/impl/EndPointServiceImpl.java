package me.vcouturier.bouchon.services.impl;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.model.Parameter;
import me.vcouturier.bouchon.properties.BouchonProperties;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import me.vcouturier.bouchon.regex.service.RegexService;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import me.vcouturier.bouchon.services.EndPointService;
import me.vcouturier.bouchon.services.FileService;
import me.vcouturier.bouchon.services.MessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class EndPointServiceImpl implements EndPointService {

    @Autowired
    private BouchonProperties properties;

    @Autowired
    private FileService fileService;

    @Autowired
    private TypeRegexService typeRegexService;

    @Autowired
    private RegexService regexService;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private MessageService messageService;

    private final Map<String, EndPoint> mapEndpoint = new HashMap<>();

    @PostConstruct
    private void init() {
        for(EndPoint e: properties.getEndpoints()){
            log.debug(messageService.formatMessage(MessageEnum.DEBUG_ENDPOINT_CREATION, e.getName()));
            try {
                // Folder initialization
                initializeFolder(e);

                // parameters map initialization
                initializeParametersMap(e);

                // Validation of the endpoint parameters
                validateEndpoint(e);

                // Creation real regex
                //String urlRegex =

                // Caching endpoints
                mapEndpoint.put(e.getName(), e);
            } catch (ApplicationException ex) {
                log.error(messageService.formatMessage(MessageEnum.ERR_ENDPOINT_INIT, e.getName(), ex.getMessage()));
            }

        }
    }

    private void validateEndpoint(EndPoint e) throws ApplicationException {
        // URL Verification
        List<String> urlRegex = verifyStringTemplate(e.getUrlTemplate(), e.getMapParameters());

        // File Template Verification
        List<String> fileRegex = verifyStringTemplate(e.getFileTemplate(), e.getMapParameters());

        // Comparing both file and url regex
        verifyTemplatesCompatibility(urlRegex, fileRegex);
    }

    private void verifyTemplatesCompatibility(List<String> urlRegex, List<String> fileRegex) throws ApplicationException {
        if(!CollectionUtils.isEqualCollection(urlRegex, fileRegex)){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.ERR_REGEX_NOT_EQUALS);
        }
    }

    private void initializeFolder(EndPoint e) throws ApplicationException {
        if(!fileService.createFolder(e.getFolderName())){
            log.debug(messageService.formatMessage(MessageEnum.DEBUG_FOLDER_NOT_CREATED, e.getFolderName()));
        }
    }

    private void initializeParametersMap(EndPoint e) throws ApplicationException {
        for(Parameter param : CollectionUtils.emptyIfNull(e.getParameters())){
            e.getMapParameters().put(
                    param.getTag(),
                    // Throws an exception if typeRegex does not exist
                    typeRegexService.getTypeRegex(param.getType())
                            .orElseThrow(() -> applicationExceptionFactory.createApplicationException(MessageEnum.ERR_UKNOWN_REGEX_TYPE, param.getType())));
        }
    }

    private List<String> verifyStringTemplate(String template, Map<String, ITypeRegex> mapEndpointRegex) throws ApplicationException {
        List<String> regexList = regexService.getRegexFromString(template);

        // Duplicate elements verification
        Optional<String> regex = me.vcouturier.bouchon.utils.CollectionUtils.getFirstDuplicatedEntry(regexList);
        if(regex.isPresent()){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.ERR_URL_DUPLICATE_REGEX, regex.get());
        }

        // Valid elements verification
        regex = regexList.stream()
                .filter(s -> mapEndpointRegex.get(s) == null)
                .findAny();
        if(regex.isPresent()){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.ERR_URL_UNKOWN_REGEX, regex.get());
        }

        return regexList;
    }

    @Override
    public Optional<EndPoint> getEndPointCalled(String endpoint) {
        return Optional.ofNullable(mapEndpoint.get(endpoint));
    }
}