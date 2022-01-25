package me.vcouturier.bouchon.services.impl;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.enums.RequestParameterPlace;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.model.Parameter;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import me.vcouturier.bouchon.regex.service.RegexService;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import me.vcouturier.bouchon.services.EndPointService;
import me.vcouturier.bouchon.services.FileService;
import me.vcouturier.bouchon.services.MessageService;
import me.vcouturier.bouchon.yaml.constructors.ListConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EndPointServiceImpl implements EndPointService {

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

    private Optional<String> initializeEndpoint(EndPoint e) {
        log.debug(messageService.formatMessage(MessageEnum.DEBUG_ENDPOINT_CREATION, e.getName()));
        try {
            // Does the endpoint already exists ?
            verifyDoubles(e);

            // Folder initialization
            initializeFolder(e);

            // parameters map initialization
            initializeParametersMap(e);

            // Validation of the endpoint parameters
            e.getParamsAvailable().addAll(validateEndpoint(e));

            // Creation real regex
            e.setUrlRegex(regexService.getRegexFormatedString(e.getUrlTemplate(), e.getMapParameters()));

            // Caching endpoints
            mapEndpoint.put(e.getName(), e);
        } catch (ApplicationException ex) {
            String message = messageService.formatMessage(MessageEnum.ERR_ENDPOINT_INIT, e.getName(), ex.getMessage());
            log.error(message);
            return Optional.of(ex.getMessage());
        }

        return Optional.empty();
    }

    private void verifyDoubles(EndPoint e) throws ApplicationException {
        if(this.mapEndpoint.containsKey(e.getName())){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.INIT_ENDPOINT_ALREADY_EXISTS, e.getName());
        }
    }

    private List<String> validateEndpoint(EndPoint e) throws ApplicationException {
        // URL Verification
        List<String> urlRegex = verifyStringTemplate(e.getUrlTemplate(), e.getMapParameters());

        // File Template Verification
        List<String> fileRegex = verifyStringTemplate(e.getFileTemplate(), e.getMapParameters());

        // Comparing both file and url regex when parameters are in URL
        if(RequestParameterPlace.URL.equals(e.getRequestParameterPlace())){
            verifyTemplatesCompatibility(urlRegex, fileRegex);
        }

        return urlRegex;
    }

    /**
     * Method allowing to check the exact equality of the tags of the template of the URL and those of the template of the file.<br/>
     * OK:
     * <ul>
     *     <li>urlTemplate : "url{numberA}"</li>
     *     <li>fileTemplate : "file{numberA}"</li>
     * </ul>
     * KO:
     * <ul>
     *     <li>urlTemplate : "url{title}"</li>
     *     <li>fileTemplate : "file{numberA}"</li>
     * </ul>
     * @param urlRegexTags {@link List} of URL template parameter tags
     * @param fileRegexTags {@link List} of file template parameter tags
     * @throws ApplicationException {@link MessageEnum#ERR_REGEX_NOT_EQUALS} If the two lists are different
     */
    private void verifyTemplatesCompatibility(List<String> urlRegexTags, List<String> fileRegexTags) throws ApplicationException {
        if(!CollectionUtils.isEqualCollection(urlRegexTags, fileRegexTags)){
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

    /**
     * Method checking that a template is valid.
     * A template is valid if it does not have the same parameter name twice and each regex is associated with a type known to the application.
     * @param template {@link String} Template to check
     * @param mapEndpointRegex {@link Map} of regex used by the endpoint
     * @return List of endpoint regex names
     * @throws ApplicationException
     * <ul>
     *     <li>{@link MessageEnum#ERR_URL_DUPLICATE_REGEX} - The template has the same parameter name twice (ie. file{name}{name})</li>
     *     <li>{@link MessageEnum#ERR_URL_UNKOWN_REGEX} - The endpoint uses a regex type unknown to the application</li>
     * </ul>
     */
    private List<String> verifyStringTemplate(String template, Map<String, ITypeRegex> mapEndpointRegex) throws ApplicationException {
        List<String> regexList = regexService.getRegexNameFromString(template);

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
    public Optional<EndPoint> getEndPointCalled(String requestedEndpoint) throws ApplicationException {
        if(StringUtils.isEmpty(requestedEndpoint)){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_REQUESTED_URL_INVALID);
        }

        List<EndPoint> availableEndpoints = mapEndpoint.values().stream()
                .filter(endPoint -> requestedEndpoint.matches(endPoint.getUrlRegex()))
                .collect(Collectors.toList());

        if(availableEndpoints.size() == 1){
            return Optional.of(availableEndpoints.iterator().next());
        } else if(availableEndpoints.isEmpty()){
            log.warn(messageService.formatMessage(MessageEnum.ENDPOINT_NOT_FOUND, requestedEndpoint));
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.ENDPOINT_NOT_UNIQUE, availableEndpoints.stream().map(EndPoint::getName).collect(Collectors.joining()));
        }

        return Optional.empty();
    }

    /**
     * Method used to retrieve the value of the configurable parts of the GET request URL
     * @param request requested URL
     * @param endPointCalled {@link EndPoint} corresponding to the requested URL
     * @return a map associating the name of the parameters of the URL to their value
     * @throws ApplicationException {@link MessageEnum#REQUEST_MISFORMATED} If the endpoint's regex fails to validate the URL
     */
    public Map<String, String> getRequestParametersGet(String request, EndPoint endPointCalled) throws ApplicationException {
        Pattern pattern = Pattern.compile(endPointCalled.getUrlRegex());
        Matcher matcher = pattern.matcher(request);
        if(matcher.find()){
            return endPointCalled.getParamsAvailable()
                    .stream()
                    .collect(Collectors.toMap(p -> p, matcher::group));
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.REQUEST_MISFORMATED, request, endPointCalled.getUrlRegex());
        }
    }

    @Override
    public String runEndpointGet(EndPoint endPoint, String request) throws ApplicationException {
        Map<String, String> requestParameters = getRequestParametersGet(request, endPoint);
        String fileName = fileService.getFileNameFromTemplate(endPoint.getFileTemplate(), requestParameters);
        Path file = fileService.getFilePath(endPoint, fileName);
        return fileService.getFileContentToString(file);
    }

    @Override
    public String runEndpointPost(EndPoint endPoint, String request, Map<String, String> params) throws ApplicationException {
        String fileName = fileService.getFileNameFromTemplate(endPoint.getFileTemplate(), params);
        Path file = fileService.getFilePath(endPoint, fileName);
        return fileService.getFileContentToString(file);
    }

    @Override
    public Map<EndPoint, Optional<String>> loadEndpointsFromFile(File endpointFile) throws IOException {
        Map<EndPoint, Optional<String>> endpointsStatut = new HashMap<>();

        log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_LOADING, endpointFile.getName()));
        List<EndPoint> endpoints;
        try(InputStream inputStream = new FileInputStream(endpointFile)){
            Yaml yamlParser = new Yaml(new ListConstructor<>(EndPoint.class));
            endpoints = yamlParser.load(inputStream);
        }

        for(EndPoint endPoint : endpoints){
            endpointsStatut.put(endPoint, initializeEndpoint(endPoint));
        }

        return endpointsStatut;
    }

    @Override
    public void reinitializeEndpoints(){
        this.mapEndpoint.clear();
    }
}
